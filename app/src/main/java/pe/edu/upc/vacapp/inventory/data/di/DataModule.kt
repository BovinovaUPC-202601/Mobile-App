package pe.edu.upc.vacapp.inventory.data.di

import pe.edu.upc.vacapp.inventory.data.remote.InventoryService
import pe.edu.upc.vacapp.inventory.data.repository.InventoryRepository
import pe.edu.upc.vacapp.shared.data.di.SharedDataModule.getRetrofit

object DataModule {
    fun getInventoryService(): InventoryService {
        return getRetrofit().create(InventoryService::class.java)
    }

    fun getInventoryRepository(): InventoryRepository {
        return InventoryRepository(getInventoryService())
    }
}
