package pe.edu.upc.vacapp.alerts.data.repository

import pe.edu.upc.vacapp.alerts.data.remote.AlertService
import pe.edu.upc.vacapp.alerts.domain.model.Alert

class AlertRepository(private val service: AlertService) {

    suspend fun getAlertsByUserId(userId: Int): List<Alert> {
        val response = service.getAlertsByUserId(userId)
        return if (response.isSuccessful)
            response.body()?.map { it.toDomain() } ?: emptyList()
        else emptyList()
    }

    suspend fun markAsRead(alertId: Int): Alert? {
        val response = service.markAsRead(alertId)
        return if (response.isSuccessful) response.body()?.toDomain() else null
    }
}
