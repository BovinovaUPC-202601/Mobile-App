package pe.edu.upc.vacapp.animal.presentation.di

import pe.edu.upc.vacapp.animal.data.di.DataModule.getAnimalRepository
import pe.edu.upc.vacapp.animal.presentation.viewmodel.AnimalViewModel
import pe.edu.upc.vacapp.shared.session.SessionScope

object PresentationModule {
    private var instance: AnimalViewModel? = null

    init {
        SessionScope.register { instance = null }
    }

    fun getAnimalViewModel(): AnimalViewModel =
        instance ?: AnimalViewModel(getAnimalRepository()).also { instance = it }
}
