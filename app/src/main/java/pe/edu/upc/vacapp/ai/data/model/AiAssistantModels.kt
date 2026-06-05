package pe.edu.upc.vacapp.ai.data.model

// ---- Requests ----

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

// ---- Responses ----

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

/** A single stored message returned by the chat-history endpoints. Role is "user" or "assistant". */
data class ChatMessageResponse(
    val role: String,
    val content: String,
    val timestamp: String
)

/** Persisted conversation returned by GET ai/general-chat and ai/bovine-chat/{bovineId}. */
data class ChatHistoryResponse(
    val conversationType: String,
    val bovineId: Int?,
    val messages: List<ChatMessageResponse> = emptyList()
)

/** Persisted photo analysis returned by GET ai/analyses/{bovineId}. */
data class BovineAnalysisResponse(
    val id: Int,
    val bovineId: Int,
    val score: Double,
    val visibleIssues: String,
    val urgency: String,
    val recommendation: String,
    val confidence: Double,
    val createdAt: String
)
