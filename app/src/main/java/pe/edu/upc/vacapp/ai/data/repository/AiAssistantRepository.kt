package pe.edu.upc.vacapp.ai.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pe.edu.upc.vacapp.ai.data.model.AnalysisResultResponse
import pe.edu.upc.vacapp.ai.data.model.AnalyzePhotoRequest
import pe.edu.upc.vacapp.ai.data.model.BovineChatRequest
import pe.edu.upc.vacapp.ai.data.model.ChatResponse
import pe.edu.upc.vacapp.ai.data.model.GeneralChatRequest
import pe.edu.upc.vacapp.ai.data.remote.AiAssistantService
import retrofit2.Response

class AiAssistantRepository(
    private val aiAssistantService: AiAssistantService
) {
    suspend fun sendGeneralChat(message: String): ChatResponse = withContext(Dispatchers.IO) {
        unwrap(
            response = aiAssistantService.sendGeneralChat(GeneralChatRequest(message)),
            action = "sending general chat message"
        )
    }

    suspend fun sendBovineChat(bovineId: Int, message: String): ChatResponse = withContext(Dispatchers.IO) {
        unwrap(
            response = aiAssistantService.sendBovineChat(BovineChatRequest(bovineId, message)),
            action = "sending bovine chat message"
        )
    }

    suspend fun analyzePhoto(bovineId: Int, imageBase64: String): AnalysisResultResponse =
        withContext(Dispatchers.IO) {
            unwrap(
                response = aiAssistantService.analyzePhoto(AnalyzePhotoRequest(bovineId, imageBase64)),
                action = "analyzing bovine photo"
            )
        }

    private fun <T> unwrap(response: Response<T>, action: String): T {
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Empty response when $action")
        }

        val error = response.errorBody()?.string().orEmpty()
        throw Exception("Error $action: ${error.ifBlank { response.code().toString() }}")
    }
}
