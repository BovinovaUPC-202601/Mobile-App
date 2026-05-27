package pe.edu.upc.vacapp.home.domain.model

data class UserInfo(
    val id: Int = 0,
    val name: String = "",
    val totalAnimals: Int = 0,
    val totalCampaigns: Int = 0,
    val totalBarns: Int = 0
    //val totalVaccines: Int = 0,
)
