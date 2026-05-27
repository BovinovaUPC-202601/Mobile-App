package pe.edu.upc.vacapp.monitoring.data.model

import com.google.gson.annotations.SerializedName
import pe.edu.upc.vacapp.monitoring.domain.model.HealthRecord

data class HealthRecordResponse(
    @SerializedName("id")          val id: Int,
    @SerializedName("bovineId")    val bovineId: Int,
    @SerializedName("deviceId")    val deviceId: String,
    @SerializedName("temperature") val temperature: Float,
    @SerializedName("heartRate")   val heartRate: Float,
    @SerializedName("isAlert")     val isAlert: Boolean,
    @SerializedName("recordedAt")  val recordedAt: String
) {
    fun toDomain() = HealthRecord(
        id          = id,
        bovineId    = bovineId,
        deviceId    = deviceId,
        temperature = temperature,
        heartRate   = heartRate,
        isAlert     = isAlert,
        recordedAt  = recordedAt
    )
}
