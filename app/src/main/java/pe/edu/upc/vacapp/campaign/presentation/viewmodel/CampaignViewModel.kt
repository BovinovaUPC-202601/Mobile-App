package pe.edu.upc.vacapp.campaign.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pe.edu.upc.vacapp.barn.domain.model.Barn
import pe.edu.upc.vacapp.campaign.data.model.BovineResponse
import pe.edu.upc.vacapp.campaign.data.repository.CampaingRepository
import pe.edu.upc.vacapp.campaign.domain.model.Campaign

class CampaignViewModel(
    private val campaingRepository: CampaingRepository
) : ViewModel() {
    private val _campaigns = MutableStateFlow<List<Campaign>>(emptyList())
    val campaigns: StateFlow<List<Campaign>> = _campaigns

    private val _barns = MutableStateFlow<List<Barn>>(emptyList())
    val barn: StateFlow<List<Barn>> = _barns
    //
    private val _animals = MutableStateFlow<List<BovineResponse>>(emptyList())
    val animals: StateFlow<List<BovineResponse>> = _animals

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _addSuccess = MutableStateFlow(false)
    val addSuccess: StateFlow<Boolean> = _addSuccess

    private val _updateSuccess = MutableStateFlow(false)
    val updateSuccess: StateFlow<Boolean> = _updateSuccess

    private val _deleteSuccess = MutableStateFlow(false)
    val deleteSuccess: StateFlow<Boolean> = _deleteSuccess

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

    fun updateCampaign(campaign: Campaign) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                campaingRepository.updateCampaign(campaign)
                _updateSuccess.value = true
                getCampaing()
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error al actualizar campaña"
                _updateSuccess.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearUpdateSuccess() {
        _updateSuccess.value = false
    }

    fun deleteCampaign(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                campaingRepository.deleteCampaign(id)
                getCampaing()
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error al eliminar campaña"
            } finally {
                _isLoading.value = false
            }
        }
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

    fun getAnimals() {
        viewModelScope.launch {
            try {
                _animals.value = campaingRepository.getAnimals()
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error al obtener bovinos"
            }
        }
    }

    fun resetErrorMessage() {
        _errorMessage.value = null
    }

    init {
        getBarns()
    }
}
