package pe.edu.upc.vacapp.ai.data.model

data class GeneralChatRequest(
    val message: String
)

data class BovineChatRequest(
    val bovineId: Int,
    val message: String
)

data class AnalyzePhotoRequest(
    val bovineId: Int,
    val imageBase64: String
)

data class ChatResponse(
    val response: String,
    val conversationType: String
)

data class AnalysisResultResponse(
    val score: Double,
    val visibleIssues: String,
    val urgency: String,
    val recommendation: String,
    val confidence: Double
)
