package pe.edu.upc.vacapp.barn.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pe.edu.upc.vacapp.barn.data.repository.BarnRepository
import pe.edu.upc.vacapp.barn.domain.model.Barn

class BarnViewModel(
    private val barnRepository: BarnRepository
) : ViewModel() {
    private val _barns = MutableStateFlow<List<Barn>>(emptyList())
    val barn: StateFlow<List<Barn>> = _barns
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _saveSuccess = MutableStateFlow<Boolean?>(null)
    val saveSuccess: StateFlow<Boolean?> = _saveSuccess

    fun addBarn(barn: Barn) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                barnRepository.addBarn(barn)
                _saveSuccess.value = true
                getBarns()
            } catch (e: Exception) {
                _saveSuccess.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getBarns() {
        viewModelScope.launch {
            _barns.value = barnRepository.getBarns()
        }
    }
    fun resetSaveSuccess() {
        _saveSuccess.value = null
    }
}