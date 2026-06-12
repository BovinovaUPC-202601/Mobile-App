package pe.edu.upc.vacapp.subscription.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pe.edu.upc.vacapp.subscription.data.repository.SubscriptionRepository
import pe.edu.upc.vacapp.subscription.domain.model.Plan
import pe.edu.upc.vacapp.subscription.domain.model.Subscription

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

    /** A plan-change action (activate / cancel) is in flight. */
    private val _actionInProgress = MutableStateFlow(false)
    val actionInProgress: StateFlow<Boolean> = _actionInProgress

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

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
