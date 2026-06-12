package pe.edu.upc.vacapp.subscription.presentation.di

import pe.edu.upc.vacapp.subscription.data.di.DataModule.getSubscriptionRepository
import pe.edu.upc.vacapp.subscription.presentation.viewmodel.SubscriptionViewModel

object PresentationModule {
    private val subscriptionViewModelInstance: SubscriptionViewModel by lazy {
        SubscriptionViewModel(getSubscriptionRepository())
    }

    fun getSubscriptionViewModel(): SubscriptionViewModel = subscriptionViewModelInstance
}
