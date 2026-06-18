package pe.edu.upc.vacapp.alerts.domain.model

data class Alert(
    val id: Int,
    val bovineId: Int?, // null for account-level alerts (e.g. CollarReturn)
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

    // Account-level alerts (not tied to a bovine), e.g. collar-return.
    val isAccountLevel: Boolean get() = bovineId == null

    // Friendly Spanish label for the alert category. The specific condition
    // (fiebre, taquicardia, …) lives in `message`, not here.
    val alertTypeLabel: String get() = when (alertType) {
        "BiometricAnomaly" -> "Anomalía biométrica"
        "VisualAnomaly"    -> "Anomalía visual"
        "CollarReturn"     -> "Devolución de collares"
        else               -> alertType
    }
}
