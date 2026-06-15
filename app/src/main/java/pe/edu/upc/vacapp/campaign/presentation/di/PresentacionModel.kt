package pe.edu.upc.vacapp.campaign.presentation.di

import pe.edu.upc.vacapp.campaign.data.di.DataModule.getCampaingRepository
import pe.edu.upc.vacapp.campaign.presentation.viewmodel.CampaignViewModel

object PresentacionModel {
    private val campaignViewModelInstance: CampaignViewModel by lazy {
        CampaignViewModel(getCampaingRepository())
    }

    fun getCampaignViewModel(): CampaignViewModel = campaignViewModelInstance
}
