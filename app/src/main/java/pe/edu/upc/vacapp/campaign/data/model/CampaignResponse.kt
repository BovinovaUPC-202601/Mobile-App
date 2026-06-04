package pe.edu.upc.vacapp.campaign.data.model

import org.threeten.bp.LocalDate
import pe.edu.upc.vacapp.campaign.domain.model.Campaign

data class CampaignResponse(
    val id: Int,
    val name: String,
    val description: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    ) {
    fun toCampaign(): Campaign {

        return Campaign(
            name,
            description,
            startDate,
            endDate,
        )
    }
}
