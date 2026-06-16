package pe.edu.upc.vacapp.campaign.data.model

import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import pe.edu.upc.vacapp.campaign.domain.model.Campaign


data class CreateCampaignRequest(
    val name: String,
    val description: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val stableIds: List<Int>,
) {
    companion object {
        fun fromCampaign(c: Campaign): CreateCampaignRequest {
            return CreateCampaignRequest(
                name = c.name,
                description = c.description,
                startDate = c.startDate,
                endDate = c.endDate,
                stableIds = c.stableIds,
            )
        }
    }
}
