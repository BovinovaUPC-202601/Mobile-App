package pe.edu.upc.vacapp.monitoring.data.repository

import pe.edu.upc.vacapp.monitoring.data.remote.MonitoringService
import pe.edu.upc.vacapp.monitoring.domain.model.HealthRecord

class MonitoringRepository(private val service: MonitoringService) {

    suspend fun getLatest(bovineId: Int): HealthRecord? {
        val response = service.getLatest(bovineId)
        return if (response.isSuccessful) response.body()?.toDomain() else null
    }

    suspend fun getHistory(bovineId: Int): List<HealthRecord> {
        val response = service.getHistory(bovineId)
        return if (response.isSuccessful)
            response.body()?.map { it.toDomain() } ?: emptyList()
        else emptyList()
    }
}
