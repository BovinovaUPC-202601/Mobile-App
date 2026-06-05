package pe.edu.upc.vacapp.monitoring.data.model

import com.google.gson.annotations.SerializedName
import pe.edu.upc.vacapp.monitoring.domain.model.HealthRecord

data class HealthRecordResponse(
    @SerializedName("id")          val id: Int,
    @SerializedName("bovineId")    val bovineId: Int,
    @SerializedName("deviceId")    val deviceId: String,
    @SerializedName("temperature") val temperature: Float,
    @SerializedName("heartRate")   val heartRate: Float,
    @SerializedName("batteryLevel") val batteryLevel: Int,
    @SerializedName("isAlert")     val isAlert: Boolean,
    @SerializedName("recordedAt")  val recordedAt: String
) {
    fun toDomain() = HealthRecord(
        id           = id,
        bovineId     = bovineId,
        deviceId     = deviceId,
        temperature  = temperature,
        heartRate    = heartRate,
        batteryLevel = batteryLevel,
        isAlert      = isAlert,
        recordedAt   = recordedAt
    )
}
