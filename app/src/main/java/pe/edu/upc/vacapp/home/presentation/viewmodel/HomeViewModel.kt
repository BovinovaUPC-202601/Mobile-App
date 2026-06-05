package pe.edu.upc.vacapp.home.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pe.edu.upc.vacapp.home.data.repository.Result
import pe.edu.upc.vacapp.home.data.repository.UserInfoRepository
import pe.edu.upc.vacapp.home.domain.model.UserInfo
import pe.edu.upc.vacapp.shared.data.local.UserStorage

class HomeViewModel(
    private val getUserInfoRepository: UserInfoRepository
) : ViewModel() {

    private val _userInfo = MutableStateFlow(UserInfo())
    val userInfo: StateFlow<UserInfo> = _userInfo

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        getUserInfo()
    }

    fun getUserInfo() {
        viewModelScope.launch {
            when (val result = getUserInfoRepository.getUserInfo()) {
                is Result.Success -> {
                    _userInfo.value = result.data
                    _errorMessage.value = null
                    // Persist the id so the alert poller/worker knows whose alerts to fetch.
                    if (result.data.id > 0) UserStorage.saveUserId(result.data.id)
                }
                is Result.Error -> {
                    _errorMessage.value = result.exception.message ?: "Error desconocido"
                }
            }
        }
    }
}
