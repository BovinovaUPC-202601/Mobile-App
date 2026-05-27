package pe.edu.upc.vacapp.alerts.data.di

import pe.edu.upc.vacapp.alerts.data.remote.AlertService
import pe.edu.upc.vacapp.alerts.data.repository.AlertRepository
import pe.edu.upc.vacapp.shared.data.di.SharedDataModule

object DataModule {
    private val service: AlertService by lazy {
        SharedDataModule.getRetrofit().create(AlertService::class.java)
    }

    val repository: AlertRepository by lazy {
        AlertRepository(service)
    }
}
