package pe.edu.upc.vacapp.monitoring.data.di

import pe.edu.upc.vacapp.monitoring.data.remote.MonitoringService
import pe.edu.upc.vacapp.monitoring.data.repository.MonitoringRepository
import pe.edu.upc.vacapp.shared.data.di.SharedDataModule

object DataModule {
    private val service: MonitoringService by lazy {
        SharedDataModule.getRetrofit().create(MonitoringService::class.java)
    }

    val repository: MonitoringRepository by lazy {
        MonitoringRepository(service)
    }
}
