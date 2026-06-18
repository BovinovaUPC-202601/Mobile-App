package pe.edu.upc.vacapp.subscription.presentation.di

import pe.edu.upc.vacapp.subscription.data.di.DataModule.getSubscriptionRepository
import pe.edu.upc.vacapp.subscription.presentation.viewmodel.SubscriptionViewModel
import pe.edu.upc.vacapp.shared.session.SessionScope

object PresentationModule {
    private var instance: SubscriptionViewModel? = null

    init {
        SessionScope.register { instance = null }
    }

    fun getSubscriptionViewModel(): SubscriptionViewModel =
        instance ?: SubscriptionViewModel(getSubscriptionRepository()).also { instance = it }
}
