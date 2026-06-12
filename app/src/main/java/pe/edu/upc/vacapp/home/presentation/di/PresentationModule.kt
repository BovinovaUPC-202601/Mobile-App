package pe.edu.upc.vacapp.home.presentation.di

import pe.edu.upc.vacapp.home.data.di.DataModule.getUserInfoRepository
import pe.edu.upc.vacapp.home.presentation.viewmodel.HomeViewModel

object PresentationModule {
    private val homeViewModelInstance: HomeViewModel by lazy {
        HomeViewModel(getUserInfoRepository())
    }

    fun getHomeViewModel(): HomeViewModel = homeViewModelInstance
}
