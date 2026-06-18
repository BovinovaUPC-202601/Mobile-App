package pe.edu.upc.vacapp.inventory.presentation.di

import pe.edu.upc.vacapp.inventory.data.di.DataModule.getInventoryRepository
import pe.edu.upc.vacapp.inventory.presentation.viewmodel.InventoryViewModel
import pe.edu.upc.vacapp.shared.session.SessionScope

object PresentationModule {
    private var instance: InventoryViewModel? = null

    init {
        SessionScope.register { instance = null }
    }

    fun getInventoryViewModel(): InventoryViewModel =
        instance ?: InventoryViewModel(getInventoryRepository()).also { instance = it }
}
