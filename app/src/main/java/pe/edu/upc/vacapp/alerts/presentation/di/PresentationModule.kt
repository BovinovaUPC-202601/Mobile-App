package pe.edu.upc.vacapp.alerts.presentation.di

import pe.edu.upc.vacapp.alerts.data.di.DataModule
import pe.edu.upc.vacapp.alerts.presentation.viewmodel.AlertViewModel
import pe.edu.upc.vacapp.animal.data.di.DataModule.getAnimalRepository
import pe.edu.upc.vacapp.shared.session.SessionScope

object PresentationModule {
    private var instance: AlertViewModel? = null

    init {
        SessionScope.register { instance = null }
    }

    fun getAlertViewModel(): AlertViewModel =
        instance ?: AlertViewModel(DataModule.repository, getAnimalRepository()).also { instance = it }
}
