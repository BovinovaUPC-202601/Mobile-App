package pe.edu.upc.vacapp.subscription.data.di

import pe.edu.upc.vacapp.shared.data.di.SharedDataModule.getRetrofit
import pe.edu.upc.vacapp.subscription.data.remote.SubscriptionService
import pe.edu.upc.vacapp.subscription.data.repository.SubscriptionRepository

object DataModule {
    fun getSubscriptionService(): SubscriptionService {
        return getRetrofit().create(SubscriptionService::class.java)
    }

    fun getSubscriptionRepository(): SubscriptionRepository {
        return SubscriptionRepository(getSubscriptionService())
    }
}
