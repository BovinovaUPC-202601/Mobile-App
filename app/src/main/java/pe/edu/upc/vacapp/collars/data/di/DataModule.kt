package pe.edu.upc.vacapp.collars.data.di

import pe.edu.upc.vacapp.collars.data.remote.CollarService
import pe.edu.upc.vacapp.collars.data.repository.CollarRepository
import pe.edu.upc.vacapp.shared.data.di.SharedDataModule.getRetrofit

object DataModule {
    fun getCollarService(): CollarService {
        return getRetrofit().create(CollarService::class.java)
    }

    fun getCollarRepository(): CollarRepository {
        return CollarRepository(getCollarService())
    }
}
