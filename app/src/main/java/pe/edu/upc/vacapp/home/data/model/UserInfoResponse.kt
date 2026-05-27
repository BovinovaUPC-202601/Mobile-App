package pe.edu.upc.vacapp.home.data.model

import pe.edu.upc.vacapp.home.domain.model.UserInfo

data class UserInfoResponse(
    val id: Int = 0,
    val name: String,
    val totalAnimals: Int,
    val totalCampaigns: Int,
    val totalStables: Int,
) {
    fun toUserInfo(): UserInfo {
        return UserInfo(
            id             = id,
            name           = name,
            totalAnimals   = totalAnimals,
            totalCampaigns = totalCampaigns,
            totalBarns     = totalStables,
        )
    }
}
