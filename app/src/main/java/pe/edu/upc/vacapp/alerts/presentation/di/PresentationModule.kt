package pe.edu.upc.vacapp.alerts.presentation.di

import pe.edu.upc.vacapp.alerts.data.di.DataModule
import pe.edu.upc.vacapp.alerts.presentation.viewmodel.AlertViewModel

object PresentationModule {
    private val alertViewModelInstance: AlertViewModel by lazy {
        AlertViewModel(DataModule.repository)
    }

    fun getAlertViewModel(): AlertViewModel = alertViewModelInstance
}
