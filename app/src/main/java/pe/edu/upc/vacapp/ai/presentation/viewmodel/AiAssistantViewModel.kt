package pe.edu.upc.vacapp.ai.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter
import pe.edu.upc.vacapp.ai.data.model.AnalysisResultResponse
import pe.edu.upc.vacapp.ai.data.repository.AiAssistantRepository
import pe.edu.upc.vacapp.animal.data.repository.AnimalRepository
import pe.edu.upc.vacapp.animal.domain.model.Animal

enum class AiChatMode {
    GENERAL,
    BOVINE
}

data class AiChatMessage(
    val id: Long,
    val isFromUser: Boolean,
    val content: String,
    val sentAt: String
)

data class AiAnalysisHistoryItem(
    val id: Long,
    val bovineName: String,
    val createdAt: String,
    val result: AnalysisResultResponse
)

class AiAssistantViewModel(
    private val aiAssistantRepository: AiAssistantRepository,
    private val animalRepository: AnimalRepository
) : ViewModel() {

    private var nextMessageId = 1L
    private var nextHistoryId = 1L

    private val _animals = MutableStateFlow<List<Animal>>(emptyList())
    val animals: StateFlow<List<Animal>> = _animals

    private val _selectedBovineId = MutableStateFlow<Int?>(null)
    val selectedBovineId: StateFlow<Int?> = _selectedBovineId

    private val _generalMessages = MutableStateFlow(
        listOf(
            nextMessage(
                isFromUser = false,
                content = "Hello. I can answer questions about your ranch, campaigns, and bovines."
            )
        )
    )
    val generalMessages: StateFlow<List<AiChatMessage>> = _generalMessages

    private val _bovineMessages = MutableStateFlow(
        listOf(
            nextMessage(
                isFromUser = false,
                content = "Select a bovine and ask me about its history, care, or current context."
            )
        )
    )
    val bovineMessages: StateFlow<List<AiChatMessage>> = _bovineMessages

    private val _analysisResult = MutableStateFlow<AnalysisResultResponse?>(null)
    val analysisResult: StateFlow<AnalysisResultResponse?> = _analysisResult

    private val _analysisHistory = MutableStateFlow<List<AiAnalysisHistoryItem>>(emptyList())
    val analysisHistory: StateFlow<List<AiAnalysisHistoryItem>> = _analysisHistory

    private val _isLoadingAnimals = MutableStateFlow(false)
    val isLoadingAnimals: StateFlow<Boolean> = _isLoadingAnimals

    private val _isChatLoading = MutableStateFlow(false)
    val isChatLoading: StateFlow<Boolean> = _isChatLoading

    private val _isAnalysisLoading = MutableStateFlow(false)
    val isAnalysisLoading: StateFlow<Boolean> = _isAnalysisLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun loadAnimals() {
        viewModelScope.launch {
            _isLoadingAnimals.value = true

            try {
                val loadedAnimals = animalRepository.getAllAnimals()
                _animals.value = loadedAnimals

                if (_selectedBovineId.value == null) {
                    _selectedBovineId.value = loadedAnimals.firstOrNull()?.id
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Could not load bovines"
            } finally {
                _isLoadingAnimals.value = false
            }
        }
    }

    fun selectBovine(bovineId: Int) {
        _selectedBovineId.value = bovineId
    }

    fun sendMessage(mode: AiChatMode, message: String) {
        val trimmedMessage = message.trim()
        if (trimmedMessage.isBlank() || _isChatLoading.value) return

        when (mode) {
            AiChatMode.GENERAL -> sendGeneralMessage(trimmedMessage)
            AiChatMode.BOVINE -> sendBovineMessage(trimmedMessage)
        }
    }

    fun analyzePhoto(imageBase64: String) {
        val bovineId = _selectedBovineId.value
        if (bovineId == null) {
            _errorMessage.value = "Select a bovine before analyzing a photo"
            return
        }

        if (imageBase64.isBlank()) {
            _errorMessage.value = "Select or capture a photo first"
            return
        }

        viewModelScope.launch {
            _isAnalysisLoading.value = true
            _errorMessage.value = null

            try {
                val result = aiAssistantRepository.analyzePhoto(bovineId, imageBase64)
                _analysisResult.value = result
                _analysisHistory.value = listOf(
                    AiAnalysisHistoryItem(
                        id = nextHistoryId++,
                        bovineName = bovineNameFor(bovineId),
                        createdAt = LocalDateTime.now().format(historyFormatter),
                        result = result
                    )
                ) + _analysisHistory.value
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Could not analyze the photo"
            } finally {
                _isAnalysisLoading.value = false
            }
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    private fun sendGeneralMessage(message: String) {
        _generalMessages.value = _generalMessages.value + nextMessage(true, message)

        viewModelScope.launch {
            _isChatLoading.value = true
            _errorMessage.value = null

            try {
                val response = aiAssistantRepository.sendGeneralChat(message)
                _generalMessages.value = _generalMessages.value + nextMessage(false, response.response)
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Could not send the message"
            } finally {
                _isChatLoading.value = false
            }
        }
    }

    private fun sendBovineMessage(message: String) {
        val bovineId = _selectedBovineId.value
        if (bovineId == null) {
            _errorMessage.value = "Select a bovine before sending the message"
            return
        }

        _bovineMessages.value = _bovineMessages.value + nextMessage(true, message)

        viewModelScope.launch {
            _isChatLoading.value = true
            _errorMessage.value = null

            try {
                val response = aiAssistantRepository.sendBovineChat(bovineId, message)
                _bovineMessages.value = _bovineMessages.value + nextMessage(false, response.response)
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Could not send the message"
            } finally {
                _isChatLoading.value = false
            }
        }
    }

    private fun nextMessage(isFromUser: Boolean, content: String): AiChatMessage {
        return AiChatMessage(
            id = nextMessageId++,
            isFromUser = isFromUser,
            content = content,
            sentAt = LocalTime.now().format(timeFormatter)
        )
    }

    private fun bovineNameFor(bovineId: Int): String {
        return _animals.value.firstOrNull { it.id == bovineId }?.name ?: "Bovine #$bovineId"
    }

    private companion object {
        val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        val historyFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
    }
}
