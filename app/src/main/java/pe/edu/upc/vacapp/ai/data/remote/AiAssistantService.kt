package pe.edu.upc.vacapp.ai.data.remote

import pe.edu.upc.vacapp.ai.data.model.AnalysisResultResponse
import pe.edu.upc.vacapp.ai.data.model.AnalyzePhotoRequest
import pe.edu.upc.vacapp.ai.data.model.BovineAnalysisResponse
import pe.edu.upc.vacapp.ai.data.model.BovineChatRequest
import pe.edu.upc.vacapp.ai.data.model.ChatHistoryResponse
import pe.edu.upc.vacapp.ai.data.model.ChatResponse
import pe.edu.upc.vacapp.ai.data.model.GeneralChatRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface AiAssistantService {
    @POST("ai/general-chat")
    suspend fun sendGeneralChat(
        @Body request: GeneralChatRequest
    ): Response<ChatResponse>

    @GET("ai/general-chat")
    suspend fun getGeneralChatHistory(): Response<ChatHistoryResponse>

    @POST("ai/bovine-chat")
    suspend fun sendBovineChat(
        @Body request: BovineChatRequest
    ): Response<ChatResponse>

    @GET("ai/bovine-chat/{bovineId}")
    suspend fun getBovineChatHistory(
        @Path("bovineId") bovineId: Int
    ): Response<ChatHistoryResponse>

    @POST("ai/analyze-photo")
    suspend fun analyzePhoto(
        @Body request: AnalyzePhotoRequest
    ): Response<AnalysisResultResponse>

    @GET("ai/analyses/{bovineId}")
    suspend fun getBovineAnalyses(
        @Path("bovineId") bovineId: Int
    ): Response<List<BovineAnalysisResponse>>
}
