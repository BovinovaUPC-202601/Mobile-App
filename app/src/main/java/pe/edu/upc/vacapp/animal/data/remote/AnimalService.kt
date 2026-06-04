package pe.edu.upc.vacapp.animal.data.remote

import okhttp3.MultipartBody
import okhttp3.RequestBody
import pe.edu.upc.vacapp.animal.data.model.AnimalResponse
import pe.edu.upc.vacapp.barn.data.model.BarnResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface AnimalService {
    /*
    string Name,
    string Gender,
    DateTime? BirthDate,
    string? Breed,
    string? Location,
    int? StableId,
    IFormFile? FileData);*/

    @Multipart
    @POST("bovines")
    suspend fun addAnimal(
        @Part("Name") name: RequestBody,
        @Part("Gender") gender: RequestBody,
        @Part("BirthDate") birthDate: RequestBody,
        @Part("Breed") breed: RequestBody,
        @Part("StableId") stableId: RequestBody,
        @Part FileData: MultipartBody.Part
    ): Response<AnimalResponse>

    @GET("bovines")
    suspend fun getAllAnimals(): Response<List<AnimalResponse>>

    @GET("stables")
    suspend fun getBarns(): Response<List<BarnResponse>>
}