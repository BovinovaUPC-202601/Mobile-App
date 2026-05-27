package pe.edu.upc.vacapp.alerts.data.model

import com.google.gson.annotations.SerializedName
import pe.edu.upc.vacapp.alerts.domain.model.Alert

data class AlertResponse(
    @SerializedName("id")           val id: Int,
    @SerializedName("bovineId")     val bovineId: Int,
    @SerializedName("userId")       val userId: Int,
    @SerializedName("alertType")    val alertType: String,
    @SerializedName("urgencyLevel") val urgencyLevel: String,
    @SerializedName("status")       val status: String,
    @SerializedName("message")      val message: String,
    @SerializedName("createdAt")    val createdAt: String
) {
    fun toDomain() = Alert(
        id           = id,
        bovineId     = bovineId,
        userId       = userId,
        alertType    = alertType,
        urgencyLevel = urgencyLevel,
        status       = status,
        message      = message,
        createdAt    = createdAt
    )
}
