package pe.edu.upc.vacapp.monitoring.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pe.edu.upc.vacapp.monitoring.data.repository.MonitoringRepository
import pe.edu.upc.vacapp.monitoring.domain.model.HealthRecord

class MonitoringViewModel(private val repository: MonitoringRepository) : ViewModel() {

    private val _latest = MutableStateFlow<HealthRecord?>(null)
    val latest: StateFlow<HealthRecord?> = _latest

    private val _history = MutableStateFlow<List<HealthRecord>>(emptyList())
    val history: StateFlow<List<HealthRecord>> = _history

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    fun loadData(bovineId: Int) {
        viewModelScope.launch {
            _loading.value = true
            _latest.value  = repository.getLatest(bovineId)
            _history.value = repository.getHistory(bovineId)
            _loading.value = false
        }
    }
}
