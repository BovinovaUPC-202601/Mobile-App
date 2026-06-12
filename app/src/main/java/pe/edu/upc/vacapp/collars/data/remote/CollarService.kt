package pe.edu.upc.vacapp.collars.data.remote

import pe.edu.upc.vacapp.collars.data.model.CapacityResponse
import pe.edu.upc.vacapp.collars.data.model.CollarResponse
import pe.edu.upc.vacapp.collars.data.model.ReassignCollarRequest
import pe.edu.upc.vacapp.collars.data.model.RegisterCollarRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface CollarService {
    @GET("iot-monitoring/collars")
    suspend fun getCollars(): Response<List<CollarResponse>>

    @GET("iot-monitoring/collars/capacity")
    suspend fun getCapacity(): Response<CapacityResponse>

    @POST("iot-monitoring/collars")
    suspend fun register(@Body request: RegisterCollarRequest): Response<CollarResponse>

    @PUT("iot-monitoring/collars/{id}")
    suspend fun reassign(
        @Path("id") id: Int,
        @Body request: ReassignCollarRequest
    ): Response<CollarResponse>

    @DELETE("iot-monitoring/collars/{id}")
    suspend fun remove(@Path("id") id: Int): Response<Unit>
}
