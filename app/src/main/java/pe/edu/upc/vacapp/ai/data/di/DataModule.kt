package pe.edu.upc.vacapp.ai.data.di

import pe.edu.upc.vacapp.ai.data.remote.AiAssistantService
import pe.edu.upc.vacapp.ai.data.repository.AiAssistantRepository
import pe.edu.upc.vacapp.shared.data.di.SharedDataModule.getRetrofit

object DataModule {
    fun getAiAssistantService(): AiAssistantService {
        return getRetrofit().create(AiAssistantService::class.java)
    }

    fun getAiAssistantRepository(): AiAssistantRepository {
        return AiAssistantRepository(getAiAssistantService())
    }
}
