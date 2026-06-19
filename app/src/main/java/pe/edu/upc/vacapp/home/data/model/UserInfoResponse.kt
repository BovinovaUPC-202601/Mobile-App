package pe.edu.upc.vacapp.home.data.model

import org.threeten.bp.LocalDate
import pe.edu.upc.vacapp.home.domain.model.NextCampaign
import pe.edu.upc.vacapp.home.domain.model.UserInfo


/*
{
  "id": 0,
  "name": "string",
  "totalAnimals": 0,
  "totalCampaigns": 0,
  "totalStaff": 0,
  "totalProducts": 0,
  "totalStables": 0,
  "nextCampaigns": [
    {
      "id": 0,
      "name": "string",
      "startDate": "2026-06-04",
      "endDate": "2026-06-04"
    }
  ]
}
*/

data class UserInfoResponse(
    val id: Int = 0,
    val name: String,
    val totalAnimals: Int,
    val totalCampaigns: Int,
    val totalStaff: Int,
    val totalProducts: Int,
    val totalStables: Int,
    val nextCampaigns: List<NextCampaignResponse>
) {
    fun toUserInfo(): UserInfo {
        return UserInfo(
            id = id,
            name = name,
            totalAnimals = totalAnimals,
            totalCampaigns = totalCampaigns,
            totalBarns = totalStables,
            totalProducts = totalProducts,
            nextCampaigns = nextCampaigns.map { c ->
                NextCampaign(
                    c.id,
                    c.name,
                    c.startDate,
                    c.endDate
                )
            }
        )
    }
}

data class NextCampaignResponse(
    val id: Int,
    val name: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
) {

}