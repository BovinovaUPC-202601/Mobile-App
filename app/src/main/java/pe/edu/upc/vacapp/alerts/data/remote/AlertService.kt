package pe.edu.upc.vacapp.alerts.data.remote

import pe.edu.upc.vacapp.alerts.data.model.AlertResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface AlertService {

    @GET("alerts")
    suspend fun getAlertsByUserId(
        @Query("userId") userId: Int
    ): Response<List<AlertResponse>>

    @GET("alerts/{alertId}")
    suspend fun getAlertById(
        @Path("alertId") alertId: Int
    ): Response<AlertResponse>

    @PUT("alerts/{alertId}/read")
    suspend fun markAsRead(
        @Path("alertId") alertId: Int
    ): Response<AlertResponse>
}
