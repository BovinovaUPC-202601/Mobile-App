package pe.edu.upc.vacapp.alerts.domain.model

data class Alert(
    val id: Int,
    val bovineId: Int,
    val userId: Int,
    val alertType: String,
    val urgencyLevel: String,
    val status: String,
    val message: String,
    val createdAt: String
) {
    val isUnread: Boolean get() = status == "Unread"
    val isRed: Boolean    get() = urgencyLevel == "Red"
    val isYellow: Boolean get() = urgencyLevel == "Yellow"
}
