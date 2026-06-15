package pe.edu.upc.vacapp.collars.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pe.edu.upc.vacapp.collars.data.repository.CollarRepository
import pe.edu.upc.vacapp.collars.data.repository.CollarsRequirePlusException
import pe.edu.upc.vacapp.collars.domain.model.Collar
import pe.edu.upc.vacapp.collars.domain.model.CollarCapacity
import pe.edu.upc.vacapp.collars.domain.model.CollarId

class CollarViewModel(
    private val repository: CollarRepository
) : ViewModel() {

    private val _collars = MutableStateFlow<List<Collar>>(emptyList())
    val collars: StateFlow<List<Collar>> = _collars

    private val _capacity = MutableStateFlow(CollarCapacity())
    val capacity: StateFlow<CollarCapacity> = _capacity

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    /** True when the backend gated collars behind Plus (403). */
    private val _requiresPlus = MutableStateFlow(false)
    val requiresPlus: StateFlow<Boolean> = _requiresPlus

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun fetchCollars() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                _capacity.value = repository.getCapacity()
                _collars.value = repository.getCollars()
                _requiresPlus.value = false
            } catch (e: CollarsRequirePlusException) {
                _requiresPlus.value = true
                _collars.value = emptyList()
                _capacity.value = CollarCapacity()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun collarForBovine(bovineId: Int): Collar? =
        _collars.value.firstOrNull { it.bovineId == bovineId }

    /** Collar numbers 1..allowance not yet taken by an active collar. */
    fun availableNumbers(): List<Int> {
        val taken = _collars.value.mapNotNull { CollarId.parseNumber(it.deviceId) }.toSet()
        // Cap by the real free slots from the backend, so collars that don't follow the
        // collar-N convention (legacy/raw ids) still consume a slot and we never over-offer.
        return (1.._capacity.value.allowance).filter { it !in taken }
            .take(_capacity.value.available.coerceAtLeast(0))
    }

    fun assign(number: Int, bovineId: Int) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                if (repository.register(CollarId.makeDeviceId(number), bovineId)) fetchCollars()
                else _error.value = "No se pudo asignar el collar."
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    /** Replaces the device on this bovine: drop the current collar, register the new number. */
    fun change(number: Int, bovineId: Int) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val current = collarForBovine(bovineId)
                if (current != null && !repository.remove(current.id)) {
                    _error.value = "No se pudo quitar el collar actual."
                    return@launch
                }
                if (repository.register(CollarId.makeDeviceId(number), bovineId)) fetchCollars()
                else _error.value = "No se pudo asignar el collar."
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun remove(collarId: Int) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                if (repository.remove(collarId)) fetchCollars()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }
}
