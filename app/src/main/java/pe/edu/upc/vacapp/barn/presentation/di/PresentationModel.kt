package pe.edu.upc.vacapp.barn.presentation.di

import pe.edu.upc.vacapp.barn.data.di.DataModule.getBarnRepository
import pe.edu.upc.vacapp.barn.presentation.viewmodel.BarnViewModel


object PresentationModel {
    private val barnViewModelInstance: BarnViewModel by lazy {
        BarnViewModel(getBarnRepository())
    }

    fun getBarnViewModel(): BarnViewModel = barnViewModelInstance
}
