package pe.edu.upc.vacapp.ai.data.remote

import pe.edu.upc.vacapp.ai.data.model.AnalysisResultResponse
import pe.edu.upc.vacapp.ai.data.model.AnalyzePhotoRequest
import pe.edu.upc.vacapp.ai.data.model.BovineChatRequest
import pe.edu.upc.vacapp.ai.data.model.ChatResponse
import pe.edu.upc.vacapp.ai.data.model.GeneralChatRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AiAssistantService {
    @POST("ai/general-chat")
    suspend fun sendGeneralChat(
        @Body request: GeneralChatRequest
    ): Response<ChatResponse>

    @POST("ai/bovine-chat")
    suspend fun sendBovineChat(
        @Body request: BovineChatRequest
    ): Response<ChatResponse>

    @POST("ai/analyze-photo")
    suspend fun analyzePhoto(
        @Body request: AnalyzePhotoRequest
    ): Response<AnalysisResultResponse>
}
