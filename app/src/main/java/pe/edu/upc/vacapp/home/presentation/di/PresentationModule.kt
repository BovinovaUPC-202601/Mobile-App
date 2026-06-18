package pe.edu.upc.vacapp.home.presentation.di

import pe.edu.upc.vacapp.home.data.di.DataModule.getUserInfoRepository
import pe.edu.upc.vacapp.home.presentation.viewmodel.HomeViewModel
import pe.edu.upc.vacapp.shared.session.SessionScope

object PresentationModule {
    private var instance: HomeViewModel? = null

    init {
        SessionScope.register { instance = null }
    }

    fun getHomeViewModel(): HomeViewModel =
        instance ?: HomeViewModel(getUserInfoRepository()).also { instance = it }
}
