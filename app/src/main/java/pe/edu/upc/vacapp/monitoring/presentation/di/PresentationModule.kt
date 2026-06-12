package pe.edu.upc.vacapp.monitoring.presentation.di

import pe.edu.upc.vacapp.monitoring.data.di.DataModule
import pe.edu.upc.vacapp.monitoring.presentation.viewmodel.MonitoringViewModel

object PresentationModule {
    private val monitoringViewModelInstance: MonitoringViewModel by lazy {
        MonitoringViewModel(DataModule.repository)
    }

    fun getMonitoringViewModel(): MonitoringViewModel = monitoringViewModelInstance
}
