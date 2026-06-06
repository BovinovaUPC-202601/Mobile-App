package pe.edu.upc.vacapp.inventory.presentation.di

import pe.edu.upc.vacapp.inventory.data.di.DataModule.getInventoryRepository
import pe.edu.upc.vacapp.inventory.presentation.viewmodel.InventoryViewModel

object PresentationModule {
    private val inventoryViewModelInstance: InventoryViewModel by lazy {
        InventoryViewModel(getInventoryRepository())
    }

    fun getInventoryViewModel(): InventoryViewModel = inventoryViewModelInstance
}
