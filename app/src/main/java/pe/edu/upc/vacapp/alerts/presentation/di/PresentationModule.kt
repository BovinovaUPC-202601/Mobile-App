package pe.edu.upc.vacapp.alerts.presentation.di

import pe.edu.upc.vacapp.alerts.data.di.DataModule
import pe.edu.upc.vacapp.alerts.presentation.viewmodel.AlertViewModel
import pe.edu.upc.vacapp.animal.data.di.DataModule.getAnimalRepository

object PresentationModule {
    private val alertViewModelInstance: AlertViewModel by lazy {
        AlertViewModel(DataModule.repository, getAnimalRepository())
    }

    fun getAlertViewModel(): AlertViewModel = alertViewModelInstance
}
