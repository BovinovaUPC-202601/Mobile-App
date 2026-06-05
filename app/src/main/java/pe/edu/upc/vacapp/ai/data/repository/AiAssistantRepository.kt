package pe.edu.upc.vacapp.ai.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pe.edu.upc.vacapp.ai.data.model.AnalysisResultResponse
import pe.edu.upc.vacapp.ai.data.model.AnalyzePhotoRequest
import pe.edu.upc.vacapp.ai.data.model.BovineAnalysisResponse
import pe.edu.upc.vacapp.ai.data.model.BovineChatRequest
import pe.edu.upc.vacapp.ai.data.model.ChatMessageResponse
import pe.edu.upc.vacapp.ai.data.model.ChatResponse
import pe.edu.upc.vacapp.ai.data.model.GeneralChatRequest
import pe.edu.upc.vacapp.ai.data.remote.AiAssistantService
import retrofit2.Response

/** The AI Assistant requires the Plus plan; the backend gates every AI endpoint with 403. */
class AiAccessDeniedException(
    message: String = "The AI Assistant is available on the Plus plan."
) : Exception(message)

/** The session token is missing or expired (401). */
class AiSessionExpiredException(
    message: String = "Your session expired. Please sign in again."
) : Exception(message)

/**
 * Talks to the AI Assistant backend (the ai endpoints).
 *
 * Open so it can be substituted by a fake in tests; the production wiring keeps using
 * [AiAssistantService] over Retrofit.
 */
open class AiAssistantRepository(
    private val aiAssistantService: AiAssistantService
) {
    open suspend fun sendGeneralChat(message: String): ChatResponse = withContext(Dispatchers.IO) {
        unwrap(
            response = aiAssistantService.sendGeneralChat(GeneralChatRequest(message)),
            action = "sending general chat message"
        )
    }

    open suspend fun sendBovineChat(bovineId: Int, message: String): ChatResponse =
        withContext(Dispatchers.IO) {
            unwrap(
                response = aiAssistantService.sendBovineChat(BovineChatRequest(bovineId, message)),
                action = "sending bovine chat message"
            )
        }

    open suspend fun analyzePhoto(bovineId: Int, imageBase64: String): AnalysisResultResponse =
        withContext(Dispatchers.IO) {
            unwrap(
                response = aiAssistantService.analyzePhoto(AnalyzePhotoRequest(bovineId, imageBase64)),
                action = "analyzing bovine photo"
            )
        }

    open suspend fun getGeneralChatHistory(): List<ChatMessageResponse> =
        withContext(Dispatchers.IO) {
            unwrap(
                response = aiAssistantService.getGeneralChatHistory(),
                action = "loading general chat history"
            ).messages
        }

    open suspend fun getBovineChatHistory(bovineId: Int): List<ChatMessageResponse> =
        withContext(Dispatchers.IO) {
            unwrap(
                response = aiAssistantService.getBovineChatHistory(bovineId),
                action = "loading bovine chat history"
            ).messages
        }

    open suspend fun getBovineAnalyses(bovineId: Int): List<BovineAnalysisResponse> =
        withContext(Dispatchers.IO) {
            unwrap(
                response = aiAssistantService.getBovineAnalyses(bovineId),
                action = "loading bovine analyses"
            )
        }

    private fun <T> unwrap(response: Response<T>, action: String): T {
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Empty response when $action")
        }

        when (response.code()) {
            401 -> throw AiSessionExpiredException()
            403 -> throw AiAccessDeniedException()
        }

        val error = response.errorBody()?.string().orEmpty()
        throw Exception("Error $action: ${error.ifBlank { response.code().toString() }}")
    }
}
