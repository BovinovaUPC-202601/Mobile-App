package pe.edu.upc.vacapp.campaign.presentation.di

import pe.edu.upc.vacapp.campaign.data.di.DataModule.getCampaingRepository
import pe.edu.upc.vacapp.campaign.presentation.viewmodel.CampaignViewModel
import pe.edu.upc.vacapp.shared.session.SessionScope

object PresentacionModel {
    private var instance: CampaignViewModel? = null

    init {
        SessionScope.register { instance = null }
    }

    fun getCampaignViewModel(): CampaignViewModel =
        instance ?: CampaignViewModel(getCampaingRepository()).also { instance = it }
}
