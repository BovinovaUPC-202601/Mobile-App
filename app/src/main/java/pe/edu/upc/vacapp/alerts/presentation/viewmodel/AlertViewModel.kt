package pe.edu.upc.vacapp.alerts.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pe.edu.upc.vacapp.alerts.data.repository.AlertRepository
import pe.edu.upc.vacapp.alerts.domain.model.Alert
import pe.edu.upc.vacapp.animal.data.repository.AnimalRepository

/** A bovine choice for the alerts filter dropdown. */
data class BovineOption(val id: Int, val name: String)

class AlertViewModel(
    private val repository: AlertRepository,
    private val animalRepository: AnimalRepository
) : ViewModel() {

    private val _alerts = MutableStateFlow<List<Alert>>(emptyList())
    val alerts: StateFlow<List<Alert>> = _alerts

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    // bovineId -> name, so cards/labels read as a bovine name instead of a raw id.
    private val _namesByBovineId = MutableStateFlow<Map<Int, String>>(emptyMap())
    val namesByBovineId: StateFlow<Map<Int, String>> = _namesByBovineId

    // null = "all bovines"; otherwise filter to a single bovine.
    private val _selectedBovineId = MutableStateFlow<Int?>(null)
    val selectedBovineId: StateFlow<Int?> = _selectedBovineId

    // Alerts after applying the bovine filter — what the screen actually renders.
    val filteredAlerts: StateFlow<List<Alert>> =
        combine(_alerts, _selectedBovineId) { alerts, selected ->
            if (selected == null) alerts else alerts.filter { it.bovineId == selected }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // Only bovines that actually have alerts, with friendly names, for the dropdown.
    val bovineOptions: StateFlow<List<BovineOption>> =
        combine(_alerts, _namesByBovineId) { alerts, names ->
            alerts.mapNotNull { it.bovineId }.distinct()
                .map { BovineOption(it, names[it] ?: "Bovino $it") }
                .sortedBy { it.name }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun loadAlerts(userId: Int) {
        viewModelScope.launch {
            _loading.value = true
            _alerts.value = repository.getAlertsByUserId(userId)
            _loading.value = false
        }
        loadAnimalNames()
    }

    fun selectBovine(bovineId: Int?) {
        _selectedBovineId.value = bovineId
    }

    fun markAsRead(alertId: Int) {
        viewModelScope.launch {
            val updated = repository.markAsRead(alertId) ?: return@launch
            _alerts.value = _alerts.value.map {
                if (it.id == alertId) updated else it
            }
        }
    }

    // Names are a nicety — on failure we silently fall back to raw bovine ids.
    private fun loadAnimalNames() {
        viewModelScope.launch {
            try {
                _namesByBovineId.value =
                    animalRepository.getAllAnimals().associate { it.id to it.name }
            } catch (_: Exception) {
            }
        }
    }
}
