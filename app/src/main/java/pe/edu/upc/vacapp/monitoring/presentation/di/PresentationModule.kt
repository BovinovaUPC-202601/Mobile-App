package pe.edu.upc.vacapp.monitoring.presentation.di

import pe.edu.upc.vacapp.monitoring.data.di.DataModule
import pe.edu.upc.vacapp.monitoring.presentation.viewmodel.MonitoringViewModel
import pe.edu.upc.vacapp.shared.session.SessionScope

object PresentationModule {
    private var instance: MonitoringViewModel? = null

    init {
        SessionScope.register { instance = null }
    }

    fun getMonitoringViewModel(): MonitoringViewModel =
        instance ?: MonitoringViewModel(DataModule.repository).also { instance = it }
}
