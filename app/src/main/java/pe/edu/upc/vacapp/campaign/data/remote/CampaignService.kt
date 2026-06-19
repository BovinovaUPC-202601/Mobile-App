package pe.edu.upc.vacapp.campaign.data.remote

import pe.edu.upc.vacapp.barn.data.model.BarnResponse
import pe.edu.upc.vacapp.campaign.data.model.BovineResponse
import pe.edu.upc.vacapp.campaign.data.model.CampaignResponse
import pe.edu.upc.vacapp.campaign.data.model.CreateCampaignRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface CampaignService {
    @POST("campaign")
    suspend fun createCampaign(
        @Body
        campaign: CreateCampaignRequest
    ): Response<Any>

    @GET("campaign/all-campaigns")
    suspend fun getCampaign(): Response<List<CampaignResponse>>

    @GET("stables")
    suspend fun getBarns(): Response<List<BarnResponse>>

    @GET("bovines")
    suspend fun getAnimals(): Response<List<BovineResponse>>
}