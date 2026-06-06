package pe.edu.upc.vacapp.ai.presentation.di

import pe.edu.upc.vacapp.ai.data.di.DataModule.getAiAssistantRepository
import pe.edu.upc.vacapp.ai.presentation.viewmodel.AiAssistantViewModel
import pe.edu.upc.vacapp.animal.data.di.DataModule.getAnimalRepository

object PresentationModule {
    private val aiAssistantViewModelInstance: AiAssistantViewModel by lazy {
        AiAssistantViewModel(getAiAssistantRepository(), getAnimalRepository())
    }

    fun getAiAssistantViewModel(): AiAssistantViewModel = aiAssistantViewModelInstance
}
