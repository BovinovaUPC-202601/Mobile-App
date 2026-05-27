package pe.edu.upc.vacapp.alerts.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pe.edu.upc.vacapp.alerts.data.repository.AlertRepository
import pe.edu.upc.vacapp.alerts.domain.model.Alert

class AlertViewModel(private val repository: AlertRepository) : ViewModel() {

    private val _alerts = MutableStateFlow<List<Alert>>(emptyList())
    val alerts: StateFlow<List<Alert>> = _alerts

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    fun loadAlerts(userId: Int) {
        viewModelScope.launch {
            _loading.value = true
            _alerts.value  = repository.getAlertsByUserId(userId)
            _loading.value = false
        }
    }

    fun markAsRead(alertId: Int) {
        viewModelScope.launch {
            val updated = repository.markAsRead(alertId) ?: return@launch
            _alerts.value = _alerts.value.map {
                if (it.id == alertId) updated else it
            }
        }
    }
}
