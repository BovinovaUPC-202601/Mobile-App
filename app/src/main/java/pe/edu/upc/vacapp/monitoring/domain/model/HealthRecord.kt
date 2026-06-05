package pe.edu.upc.vacapp.monitoring.domain.model

data class HealthRecord(
    val id: Int,
    val bovineId: Int,
    val deviceId: String,
    val temperature: Float,
    val heartRate: Float,
    val batteryLevel: Int,
    val isAlert: Boolean,
    val recordedAt: String
) {
    companion object {
        const val MIN_TEMPERATURE = 38.0f
        const val MAX_TEMPERATURE = 39.5f
        const val MIN_HEART_RATE  = 40.0f
        const val MAX_HEART_RATE  = 80.0f
    }
}
