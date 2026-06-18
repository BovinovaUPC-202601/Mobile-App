package pe.edu.upc.vacapp.ai.presentation.di

import pe.edu.upc.vacapp.ai.data.di.DataModule.getAiAssistantRepository
import pe.edu.upc.vacapp.ai.presentation.viewmodel.AiAssistantViewModel
import pe.edu.upc.vacapp.animal.data.di.DataModule.getAnimalRepository
import pe.edu.upc.vacapp.shared.session.SessionScope

object PresentationModule {
    private var instance: AiAssistantViewModel? = null

    init {
        SessionScope.register { instance = null }
    }

    fun getAiAssistantViewModel(): AiAssistantViewModel =
        instance ?: AiAssistantViewModel(getAiAssistantRepository(), getAnimalRepository())
            .also { instance = it }
}
