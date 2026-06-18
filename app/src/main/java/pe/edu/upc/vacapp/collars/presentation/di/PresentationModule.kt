package pe.edu.upc.vacapp.collars.presentation.di

import pe.edu.upc.vacapp.collars.data.di.DataModule.getCollarRepository
import pe.edu.upc.vacapp.collars.presentation.viewmodel.CollarViewModel
import pe.edu.upc.vacapp.shared.session.SessionScope

object PresentationModule {
    private var instance: CollarViewModel? = null

    init {
        SessionScope.register { instance = null }
    }

    fun getCollarViewModel(): CollarViewModel =
        instance ?: CollarViewModel(getCollarRepository()).also { instance = it }
}
