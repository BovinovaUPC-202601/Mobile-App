package pe.edu.upc.vacapp.barn.presentation.di

import pe.edu.upc.vacapp.barn.data.di.DataModule.getBarnRepository
import pe.edu.upc.vacapp.barn.presentation.viewmodel.BarnViewModel
import pe.edu.upc.vacapp.shared.session.SessionScope


object PresentationModel {
    private var instance: BarnViewModel? = null

    init {
        SessionScope.register { instance = null }
    }

    fun getBarnViewModel(): BarnViewModel =
        instance ?: BarnViewModel(getBarnRepository()).also { instance = it }
}
