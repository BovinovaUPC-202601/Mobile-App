package pe.edu.upc.vacapp.campaign.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pe.edu.upc.vacapp.barn.domain.model.Barn
import pe.edu.upc.vacapp.campaign.data.repository.CampaingRepository
import pe.edu.upc.vacapp.campaign.domain.model.Campaign

class CampaignViewModel(
    private val campaingRepository: CampaingRepository
) : ViewModel() {
    private val _campaigns = MutableStateFlow<List<Campaign>>(emptyList())
    val campaigns: StateFlow<List<Campaign>> = _campaigns

    private val _barns = MutableStateFlow<List<Barn>>(emptyList())
    val barn: StateFlow<List<Barn>> = _barns

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _addSuccess = MutableStateFlow(false)
    val addSuccess: StateFlow<Boolean> = _addSuccess

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun addCanpaing(campaign: Campaign) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                campaingRepository.addCampaing(campaign)
                _addSuccess.value = true
                getCampaing()
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error al añadir campaña"
                _addSuccess.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }
    fun resetAddSuccess() {
        _addSuccess.value = false
    }
    fun getCampaing() {
        viewModelScope.launch {
            try {
                _campaigns.value = campaingRepository.getCampaing()
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error al obtener campañas"
            }
        }
    }

    fun getBarns() {
        viewModelScope.launch {
            try {
                _barns.value = campaingRepository.getBarns()
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error al obtener establos"
            }
        }
    }

    fun resetErrorMessage() {
        _errorMessage.value = null
    }
}
