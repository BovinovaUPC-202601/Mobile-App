package pe.edu.upc.vacapp.animal.data.remote

import okhttp3.MultipartBody
import okhttp3.RequestBody
import pe.edu.upc.vacapp.animal.data.model.AnimalResponse
import pe.edu.upc.vacapp.animal.data.model.BreedRequest
import pe.edu.upc.vacapp.animal.data.model.BreedResponse
import pe.edu.upc.vacapp.animal.data.model.UpdateAnimalRequest
import pe.edu.upc.vacapp.barn.data.model.BarnResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

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
        @Part("MinTemperature") minTemperature: RequestBody,
        @Part("MaxTemperature") maxTemperature: RequestBody,
        @Part("MinHeartRate") minHeartRate: RequestBody,
        @Part("MaxHeartRate") maxHeartRate: RequestBody,
        @Part FileData: MultipartBody.Part,
    ): Response<AnimalResponse>

    @PUT("bovines/{id}")
    suspend fun updateAnimal(
        @Path("id") id: Int,
        @Body request: UpdateAnimalRequest
    ): Response<AnimalResponse>

    @GET("bovines")
    suspend fun getAllAnimals(): Response<List<AnimalResponse>>

    @GET("stables")
    suspend fun getBarns(): Response<List<BarnResponse>>

    @GET("bovines/breeds")
    suspend fun getBreeds(): Response<List<BreedResponse>>

    @POST("bovines/breeds")
    suspend fun createBreed(@Body request: BreedRequest): Response<BreedResponse>

    @PUT("bovines/breeds/{id}")
    suspend fun updateBreed(@Path("id") id: Int, @Body request: BreedRequest): Response<BreedResponse>

    @DELETE("bovines/breeds/{id}")
    suspend fun deleteBreed(@Path("id") id: Int): Response<Unit>

    @DELETE("bovines/{id}")
    suspend fun deleteAnimal(@Path("id") id: Int): Response<Unit>
}