package pe.edu.upc.vacapp.collars.presentation.di

import pe.edu.upc.vacapp.collars.data.di.DataModule.getCollarRepository
import pe.edu.upc.vacapp.collars.presentation.viewmodel.CollarViewModel

object PresentationModule {
    private val collarViewModelInstance: CollarViewModel by lazy {
        CollarViewModel(getCollarRepository())
    }

    fun getCollarViewModel(): CollarViewModel = collarViewModelInstance
}
