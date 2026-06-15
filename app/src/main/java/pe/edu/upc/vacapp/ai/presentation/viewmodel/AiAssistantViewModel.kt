package pe.edu.upc.vacapp.ai.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter
import pe.edu.upc.vacapp.ai.data.model.AnalysisResultResponse
import pe.edu.upc.vacapp.ai.data.model.BovineAnalysisResponse
import pe.edu.upc.vacapp.ai.data.model.ChatMessageResponse
import pe.edu.upc.vacapp.ai.data.repository.AiAccessDeniedException
import pe.edu.upc.vacapp.ai.data.repository.AiAssistantRepository
import pe.edu.upc.vacapp.ai.data.repository.AiSessionExpiredException
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

    private val _animals = MutableStateFlow<List<Animal>>(emptyList())
    val animals: StateFlow<List<Animal>> = _animals

    private val _selectedBovineId = MutableStateFlow<Int?>(null)
    val selectedBovineId: StateFlow<Int?> = _selectedBovineId

    private val _generalMessages = MutableStateFlow(listOf(generalGreeting()))
    val generalMessages: StateFlow<List<AiChatMessage>> = _generalMessages

    private val _bovineMessages = MutableStateFlow(listOf(bovinePlaceholder()))
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

    /** True once the backend reports the AI Assistant is gated behind the Plus plan (HTTP 403). */
    private val _requiresPlus = MutableStateFlow(false)
    val requiresPlus: StateFlow<Boolean> = _requiresPlus

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

            // Restore the persisted conversations/analyses (these are the gated ai/* endpoints).
            loadGeneralHistorySuspending()
            _selectedBovineId.value?.let { if (!_requiresPlus.value) loadBovineContextSuspending(it) }
        }
    }

    fun selectBovine(bovineId: Int) {
        if (_selectedBovineId.value == bovineId) return
        _selectedBovineId.value = bovineId

        if (_requiresPlus.value) return
        viewModelScope.launch { loadBovineContextSuspending(bovineId) }
    }

    fun sendMessage(mode: AiChatMode, message: String) {
        val trimmedMessage = message.trim()
        if (trimmedMessage.isBlank() || _isChatLoading.value || _requiresPlus.value) return

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

        if (_requiresPlus.value) return

        viewModelScope.launch {
            _isAnalysisLoading.value = true
            _errorMessage.value = null

            try {
                val result = aiAssistantRepository.analyzePhoto(bovineId, imageBase64)
                _analysisResult.value = result
                refreshAnalyses(bovineId, result)
            } catch (e: Exception) {
                handleAiException(e, "Could not analyze the photo")
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
                handleAiException(e, "Could not send the message")
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
                handleAiException(e, "Could not send the message")
            } finally {
                _isChatLoading.value = false
            }
        }
    }

    private suspend fun loadGeneralHistorySuspending() {
        try {
            val history = aiAssistantRepository.getGeneralChatHistory()
            if (history.isNotEmpty()) {
                _generalMessages.value = history.map(::toUiMessage)
            }
        } catch (e: AiAccessDeniedException) {
            _requiresPlus.value = true
        } catch (e: AiSessionExpiredException) {
            _errorMessage.value = e.message
        } catch (_: Exception) {
            // History is best-effort: keep the greeting if it cannot be restored.
        }
    }

    private suspend fun loadBovineContextSuspending(bovineId: Int) {
        try {
            val history = aiAssistantRepository.getBovineChatHistory(bovineId)
            _bovineMessages.value =
                if (history.isEmpty()) listOf(bovineIntro(bovineId)) else history.map(::toUiMessage)
        } catch (e: AiAccessDeniedException) {
            _requiresPlus.value = true
            return
        } catch (e: AiSessionExpiredException) {
            _errorMessage.value = e.message
        } catch (_: Exception) {
            _bovineMessages.value = listOf(bovineIntro(bovineId))
        }

        try {
            _analysisHistory.value = aiAssistantRepository.getBovineAnalyses(bovineId).map(::toHistoryItem)
        } catch (e: AiAccessDeniedException) {
            _requiresPlus.value = true
        } catch (_: Exception) {
            _analysisHistory.value = emptyList()
        }
    }

    private suspend fun refreshAnalyses(bovineId: Int, latest: AnalysisResultResponse) {
        try {
            _analysisHistory.value = aiAssistantRepository.getBovineAnalyses(bovineId).map(::toHistoryItem)
        } catch (_: Exception) {
            // Fall back to a local entry so the just-completed analysis is still visible.
            _analysisHistory.value = listOf(
                AiAnalysisHistoryItem(
                    id = System.currentTimeMillis(),
                    bovineName = bovineNameFor(bovineId),
                    createdAt = LocalDateTime.now().format(historyFormatter),
                    result = latest
                )
            ) + _analysisHistory.value
        }
    }

    private fun handleAiException(e: Exception, fallback: String) {
        when (e) {
            is AiAccessDeniedException -> {
                _requiresPlus.value = true
                _errorMessage.value = e.message
            }

            is AiSessionExpiredException -> _errorMessage.value = e.message
            else -> _errorMessage.value = e.message ?: fallback
        }
    }

    private fun toUiMessage(message: ChatMessageResponse): AiChatMessage {
        return AiChatMessage(
            id = nextMessageId++,
            isFromUser = message.role.equals("user", ignoreCase = true),
            content = message.content,
            sentAt = formatClock(message.timestamp)
        )
    }

    private fun toHistoryItem(analysis: BovineAnalysisResponse): AiAnalysisHistoryItem {
        return AiAnalysisHistoryItem(
            id = analysis.id.toLong(),
            bovineName = bovineNameFor(analysis.bovineId),
            createdAt = formatDateTime(analysis.createdAt),
            result = AnalysisResultResponse(
                score = analysis.score,
                visibleIssues = analysis.visibleIssues,
                urgency = analysis.urgency,
                recommendation = analysis.recommendation,
                confidence = analysis.confidence
            )
        )
    }

    private fun nextMessage(isFromUser: Boolean, content: String): AiChatMessage {
        return AiChatMessage(
            id = nextMessageId++,
            isFromUser = isFromUser,
            content = content,
            sentAt = LocalTime.now().format(timeFormatter)
        )
    }

    private fun generalGreeting(): AiChatMessage = nextMessage(
        isFromUser = false,
        content = "Hello. I can answer questions about your ranch, campaigns, and bovines."
    )

    private fun bovinePlaceholder(): AiChatMessage = nextMessage(
        isFromUser = false,
        content = "Select a bovine and ask me about its history, care, or current context."
    )

    private fun bovineIntro(bovineId: Int): AiChatMessage = nextMessage(
        isFromUser = false,
        content = "Ask me anything about ${bovineNameFor(bovineId)}: its history, care, or current context."
    )

    private fun bovineNameFor(bovineId: Int): String {
        return _animals.value.firstOrNull { it.id == bovineId }?.name ?: "Bovine #$bovineId"
    }

    private fun formatClock(raw: String): String {
        return parseDateTime(raw)?.toLocalTime()?.format(timeFormatter).orEmpty()
    }

    private fun formatDateTime(raw: String): String {
        return parseDateTime(raw)?.format(historyFormatter) ?: raw
    }

    private fun parseDateTime(raw: String): LocalDateTime? {
        return try {
            OffsetDateTime.parse(raw).toLocalDateTime()
        } catch (e: Exception) {
            try {
                LocalDateTime.parse(raw)
            } catch (e2: Exception) {
                null
            }
        }
    }

    private companion object {
        val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        val historyFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
    }
}
