package pe.edu.upc.vacapp.campaign.data.model

import pe.edu.upc.vacapp.campaign.domain.model.Campaign

data class UpdateCampaignRequest(
    val name: String,
    val description: String,
    val startDate: String,
    val endDate: String,
    val stableIds: List<Int>,
    val bovineIds: List<Int>,
) {
    companion object {
        fun fromCampaign(c: Campaign): UpdateCampaignRequest {
            return UpdateCampaignRequest(
                name = c.name,
                description = c.description,
                startDate = c.startDate.toString(),
                endDate = c.endDate.toString(),
                stableIds = c.stableIds,
                bovineIds = c.bovineIds,
            )
        }
    }
}
