package pe.edu.upc.vacapp.monitoring.data.remote

import pe.edu.upc.vacapp.monitoring.data.model.HealthRecordResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface MonitoringService {

    @GET("iot-monitoring/bovines/{bovineId}/latest")
    suspend fun getLatest(
        @Path("bovineId") bovineId: Int
    ): Response<HealthRecordResponse>

    @GET("iot-monitoring/bovines/{bovineId}/records")
    suspend fun getHistory(
        @Path("bovineId") bovineId: Int
    ): Response<List<HealthRecordResponse>>
}
