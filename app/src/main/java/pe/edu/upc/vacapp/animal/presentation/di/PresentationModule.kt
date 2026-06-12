package pe.edu.upc.vacapp.animal.presentation.di

import pe.edu.upc.vacapp.animal.data.di.DataModule.getAnimalRepository
import pe.edu.upc.vacapp.animal.presentation.viewmodel.AnimalViewModel

object PresentationModule {
    private val animalViewModelInstance: AnimalViewModel by lazy {
        AnimalViewModel(getAnimalRepository())
    }

    fun getAnimalViewModel(): AnimalViewModel = animalViewModelInstance
}
