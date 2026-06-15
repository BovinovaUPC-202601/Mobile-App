package pe.edu.upc.vacapp.ai

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import pe.edu.upc.vacapp.ai.data.model.AnalysisResultResponse
import pe.edu.upc.vacapp.ai.data.model.BovineAnalysisResponse
import pe.edu.upc.vacapp.ai.data.model.ChatMessageResponse
import pe.edu.upc.vacapp.ai.data.model.ChatResponse
import pe.edu.upc.vacapp.ai.data.remote.AiAssistantService
import pe.edu.upc.vacapp.ai.data.repository.AiAccessDeniedException
import pe.edu.upc.vacapp.ai.data.repository.AiAssistantRepository
import pe.edu.upc.vacapp.ai.presentation.viewmodel.AiAssistantViewModel
import pe.edu.upc.vacapp.ai.presentation.viewmodel.AiChatMode
import pe.edu.upc.vacapp.animal.data.remote.AnimalService
import pe.edu.upc.vacapp.animal.data.repository.AnimalRepository
import pe.edu.upc.vacapp.animal.domain.model.Animal
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Unit tests for [AiAssistantViewModel] using in-memory fakes (no network).
 * They lock in the plan-gating behaviour and the persisted-history wiring added for the
 * Plus-only AI adaptation.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class AiAssistantViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    private val animals = listOf(
        Animal(id = 1, name = "Lola", breed = "Angus"),
        Animal(id = 2, name = "Manchas", breed = "Brahman")
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun viewModel(repo: FakeAiAssistantRepository): AiAssistantViewModel =
        AiAssistantViewModel(repo, FakeAnimalRepository(animals))

    @Test
    fun `loadAnimals loads bovines and auto-selects the first`() = runTest(dispatcher) {
        val repo = FakeAiAssistantRepository()
        val vm = viewModel(repo)

        vm.loadAnimals()
        advanceUntilIdle()

        assertEquals(animals, vm.animals.value)
        assertEquals(1, vm.selectedBovineId.value)
        assertFalse(vm.requiresPlus.value)
    }

    @Test
    fun `loadAnimals restores the persisted general history`() = runTest(dispatcher) {
        val repo = FakeAiAssistantRepository().apply {
            generalHistory = listOf(
                ChatMessageResponse("user", "Hi", "2026-06-05T10:00:00Z"),
                ChatMessageResponse("assistant", "Hello rancher", "2026-06-05T10:00:01Z")
            )
        }
        val vm = viewModel(repo)

        vm.loadAnimals()
        advanceUntilIdle()

        val messages = vm.generalMessages.value
        assertEquals(2, messages.size)
        assertTrue(messages[0].isFromUser)
        assertEquals("Hello rancher", messages[1].content)
        assertFalse(messages[1].isFromUser)
    }

    @Test
    fun `sending a general message appends the user prompt and the AI reply`() = runTest(dispatcher) {
        val repo = FakeAiAssistantRepository().apply { generalReply = "42 bovines" }
        val vm = viewModel(repo)
        vm.loadAnimals()
        advanceUntilIdle()

        val before = vm.generalMessages.value.size
        vm.sendMessage(AiChatMode.GENERAL, "How many bovines?")
        advanceUntilIdle()

        val after = vm.generalMessages.value
        assertEquals(before + 2, after.size)
        assertEquals("How many bovines?", after[after.lastIndex - 1].content)
        assertEquals("42 bovines", after.last().content)
        assertEquals(listOf("How many bovines?"), repo.sentGeneralMessages)
    }

    @Test
    fun `a 403 on load marks the assistant as Plus-only`() = runTest(dispatcher) {
        val repo = FakeAiAssistantRepository().apply { accessDenied = true }
        val vm = viewModel(repo)

        vm.loadAnimals()
        advanceUntilIdle()

        assertTrue(vm.requiresPlus.value)
        // Animals come from a non-gated endpoint, so the selector still populates.
        assertEquals(animals, vm.animals.value)
    }

    @Test
    fun `when locked, sending a message is a no-op`() = runTest(dispatcher) {
        val repo = FakeAiAssistantRepository().apply { accessDenied = true }
        val vm = viewModel(repo)
        vm.loadAnimals()
        advanceUntilIdle()

        val before = vm.generalMessages.value.size
        vm.sendMessage(AiChatMode.GENERAL, "Hello?")
        advanceUntilIdle()

        assertEquals(before, vm.generalMessages.value.size)
        assertTrue(repo.sentGeneralMessages.isEmpty())
    }

    @Test
    fun `a 403 while sending sets requiresPlus`() = runTest(dispatcher) {
        val repo = FakeAiAssistantRepository()
        val vm = viewModel(repo)
        vm.loadAnimals()
        advanceUntilIdle()

        repo.accessDenied = true
        vm.sendMessage(AiChatMode.GENERAL, "Hello?")
        advanceUntilIdle()

        assertTrue(vm.requiresPlus.value)
    }

    @Test
    fun `analyzePhoto stores the result and refreshes the analysis history`() = runTest(dispatcher) {
        val repo = FakeAiAssistantRepository().apply {
            analysisResult = AnalysisResultResponse(3.5, "limping", "Medium", "Call a vet", 0.8)
            analyses = listOf(
                BovineAnalysisResponse(10, 1, 3.5, "limping", "Medium", "Call a vet", 0.8, "2026-06-05T09:00:00Z")
            )
        }
        val vm = viewModel(repo)
        vm.loadAnimals()
        advanceUntilIdle()

        vm.analyzePhoto("base64-image")
        advanceUntilIdle()

        assertEquals("limping", vm.analysisResult.value?.visibleIssues)
        assertEquals(1, vm.analysisHistory.value.size)
        assertEquals("Lola", vm.analysisHistory.value.first().bovineName)
    }

    @Test
    fun `selecting a bovine loads that bovine's persisted conversation`() = runTest(dispatcher) {
        val repo = FakeAiAssistantRepository().apply {
            bovineHistory = listOf(
                ChatMessageResponse("user", "Is Manchas healthy?", "2026-06-05T11:00:00Z"),
                ChatMessageResponse("assistant", "Vitals look normal", "2026-06-05T11:00:01Z")
            )
        }
        val vm = viewModel(repo)
        vm.loadAnimals()
        advanceUntilIdle()

        vm.selectBovine(2)
        advanceUntilIdle()

        val messages = vm.bovineMessages.value
        assertEquals(2, messages.size)
        assertEquals("Vitals look normal", messages.last().content)
        assertEquals(2, repo.lastBovineHistoryRequest)
    }
}

/** In-memory [AiAssistantRepository]; overrides every call so no Retrofit traffic happens. */
private class FakeAiAssistantRepository : AiAssistantRepository(dummyService<AiAssistantService>()) {

    var accessDenied = false
    var generalReply = "general reply"
    var bovineReply = "bovine reply"
    var generalHistory: List<ChatMessageResponse> = emptyList()
    var bovineHistory: List<ChatMessageResponse> = emptyList()
    var analyses: List<BovineAnalysisResponse> = emptyList()
    var analysisResult = AnalysisResultResponse(4.0, "none", "Low", "Keep monitoring", 0.9)

    val sentGeneralMessages = mutableListOf<String>()
    var lastBovineHistoryRequest: Int? = null

    override suspend fun sendGeneralChat(message: String): ChatResponse {
        denyIfNeeded()
        sentGeneralMessages.add(message)
        return ChatResponse(generalReply, "GENERAL")
    }

    override suspend fun sendBovineChat(bovineId: Int, message: String): ChatResponse {
        denyIfNeeded()
        return ChatResponse(bovineReply, "BOVINE")
    }

    override suspend fun analyzePhoto(bovineId: Int, imageBase64: String): AnalysisResultResponse {
        denyIfNeeded()
        return analysisResult
    }

    override suspend fun getGeneralChatHistory(): List<ChatMessageResponse> {
        denyIfNeeded()
        return generalHistory
    }

    override suspend fun getBovineChatHistory(bovineId: Int): List<ChatMessageResponse> {
        denyIfNeeded()
        lastBovineHistoryRequest = bovineId
        return bovineHistory
    }

    override suspend fun getBovineAnalyses(bovineId: Int): List<BovineAnalysisResponse> {
        denyIfNeeded()
        return analyses
    }

    private fun denyIfNeeded() {
        if (accessDenied) throw AiAccessDeniedException()
    }
}

private class FakeAnimalRepository(
    private val data: List<Animal>
) : AnimalRepository(dummyService<AnimalService>()) {
    override suspend fun getAllAnimals(): List<Animal> = data
}

/** Builds a no-traffic Retrofit proxy; used only to satisfy the repository constructors. */
private inline fun <reified T : Any> dummyService(): T =
    Retrofit.Builder()
        .baseUrl("http://localhost/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(T::class.java)
