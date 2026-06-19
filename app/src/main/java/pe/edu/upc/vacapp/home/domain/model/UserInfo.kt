package pe.edu.upc.vacapp.home.domain.model

import org.threeten.bp.LocalDate


data class UserInfo(
    val id: Int = 0,
    val name: String = "",
    val totalAnimals: Int = 0,
    val totalCampaigns: Int = 0,
    val totalBarns: Int = 0,
    val totalProducts: Int = 0,
    val nextCampaigns: List<NextCampaign> = emptyList()
)

data class NextCampaign(
    val id: Int,
    val name: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
)