package pe.edu.upc.vacapp.campaign.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pe.edu.upc.vacapp.barn.domain.model.Barn
import pe.edu.upc.vacapp.campaign.data.model.CreateCampaignRequest
import pe.edu.upc.vacapp.campaign.data.remote.CampaignService
import pe.edu.upc.vacapp.campaign.domain.model.Campaign
import pe.edu.upc.vacapp.shared.data.remote.errorMessage

class CampaingRepository(
    private val campaignService: CampaignService
) {
    suspend fun addCampaing(
        campaing: Campaign
    ) = withContext(Dispatchers.IO) {
        val data = CreateCampaignRequest.fromCampaign(campaing)
        val response = campaignService.createCampaign(data)
        if (response.isSuccessful) {
            response.body()
        } else {
            throw Exception(response.errorMessage())
        }
    }

    suspend fun getCampaing(): List<Campaign> = withContext(Dispatchers.IO) {
        val response = campaignService.getCampaign()

        if (response.isSuccessful) {
            return@withContext response.body()?.map {
                it.toCampaign()
            } ?: emptyList()
        }

        throw Exception(response.errorMessage())
    }

    suspend fun getBarns(): List<Barn> = withContext(Dispatchers.IO) {
        val response = campaignService.getBarns()

        if (response.isSuccessful) {
            return@withContext response.body()?.map {
                it.toBarn()
            } ?: emptyList()
        }

        throw Exception(response.errorMessage())
    }
}