package pe.edu.upc.vacapp.subscription.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import pe.edu.upc.vacapp.subscription.data.repository.SubscriptionRepository
import pe.edu.upc.vacapp.subscription.domain.model.CheckoutSession
import pe.edu.upc.vacapp.subscription.domain.model.Plan
import pe.edu.upc.vacapp.subscription.domain.model.Subscription

/** Phases of the native card form, mirroring the web MockCheckoutPage. */
enum class CheckoutPhase { Form, Processing, Success }

class SubscriptionViewModel(
    private val repository: SubscriptionRepository
) : ViewModel() {

    private val _plans = MutableStateFlow<List<Plan>>(emptyList())
    val plans: StateFlow<List<Plan>> = _plans

    private val _current = MutableStateFlow<Subscription?>(null)
    val current: StateFlow<Subscription?> = _current

    /** Loading the screen data (plans + current plan). */
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    /** A plan-change action (activate / cancel / checkout start) is in flight. */
    private val _actionInProgress = MutableStateFlow(false)
    val actionInProgress: StateFlow<Boolean> = _actionInProgress

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    /** Active checkout session (null = card screen hidden, showing plans). */
    private val _checkout = MutableStateFlow<CheckoutSession?>(null)
    val checkout: StateFlow<CheckoutSession?> = _checkout

    private val _checkoutPhase = MutableStateFlow(CheckoutPhase.Form)
    val checkoutPhase: StateFlow<CheckoutPhase> = _checkoutPhase

    private val _checkoutError = MutableStateFlow<String?>(null)
    val checkoutError: StateFlow<String?> = _checkoutError

    fun load() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                _plans.value = repository.getPlans()
                _current.value = repository.getCurrent()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    /** Activates Plus, then refreshes the current subscription. */
    fun activatePlus() {
        if (_actionInProgress.value) return
        viewModelScope.launch {
            _actionInProgress.value = true
            _error.value = null
            try {
                _current.value = repository.activatePlus()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _actionInProgress.value = false
            }
        }
    }

    /** Opens the Plus checkout: gets a session from the backend, then shows the card form. */
    fun startPlusCheckout() = startCheckout { repository.createPlusCheckout() }

    /** Opens the additional-collar checkout. */
    fun startCollarCheckout() = startCheckout { repository.createCollarCheckout() }

    private fun startCheckout(create: suspend () -> CheckoutSession) {
        if (_actionInProgress.value) return
        viewModelScope.launch {
            _actionInProgress.value = true
            _error.value = null
            try {
                _checkout.value = create()
                _checkoutPhase.value = CheckoutPhase.Form
                _checkoutError.value = null
            } catch (e: Exception) {
                _error.value = e.message ?: "No se pudo iniciar el pago."
            } finally {
                _actionInProgress.value = false
            }
        }
    }

    /**
     * Confirms the active checkout (after the fake card form). Min 1.6s "processing" so
     * the loader is perceived, then shows success and refreshes the subscription.
     */
    fun confirmCheckout() {
        val session = _checkout.value ?: return
        if (_checkoutPhase.value == CheckoutPhase.Processing) return
        viewModelScope.launch {
            _checkoutPhase.value = CheckoutPhase.Processing
            _checkoutError.value = null
            try {
                repository.confirmCheckout(session.sessionRef)
                delay(1600)
                _current.value = repository.getCurrent() // unlock Plus / refresh collar count
                _checkoutPhase.value = CheckoutPhase.Success
                delay(2000)
                _checkout.value = null                   // back to the plans screen
            } catch (e: Exception) {
                _checkoutError.value = "No se pudo confirmar el pago. Intentá de nuevo."
                _checkoutPhase.value = CheckoutPhase.Form
            }
        }
    }

    /** Closes the card form without paying. */
    fun cancelCheckout() {
        _checkout.value = null
        _checkoutPhase.value = CheckoutPhase.Form
        _checkoutError.value = null
    }

    /** Cancels Plus (back to Free), then refreshes the current subscription. */
    fun cancel() {
        if (_actionInProgress.value) return
        viewModelScope.launch {
            _actionInProgress.value = true
            _error.value = null
            try {
                repository.cancel()
                _current.value = repository.getCurrent()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _actionInProgress.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
