package pe.edu.upc.vacapp.home.data.remote

import pe.edu.upc.vacapp.home.data.model.UserInfoResponse
import retrofit2.Response
import retrofit2.http.GET

interface UserInfoService {
    @GET("user/profile")
    suspend fun getUserInfo(): Response<UserInfoResponse>
}