package pe.edu.upc.vacapp.iam.presentation.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pe.edu.upc.vacapp.iam.data.repository.AuthRepository
import pe.edu.upc.vacapp.iam.domain.model.User

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _user = MutableStateFlow(User())
    val user: StateFlow<User> = _user
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _loginSuccess = MutableStateFlow<Boolean?>(null)
    val loginSuccess: StateFlow<Boolean?> = _loginSuccess

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun updateEmail(email: String) {
        _user.value = _user.value.copy(email = email)
    }

    fun updatePassword(password: String) {
        _user.value = _user.value.copy(password = password)
    }

    fun updateUsername(username: String) {
        _user.value = _user.value.copy(username = username)
    }

    fun clearUser() {
        _user.value = User()
    }

    fun login() {
        val email = _user.value.email
        val password = _user.value.password

        if (email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _errorMessage.value = "El email no es válido."
            return
        }
        if (password.isBlank() || password.length < 3) {
            _errorMessage.value = "La contraseña debe tener al menos 3 caracteres."
            return
        }

        viewModelScope.launch {
            try {
                _isLoading.value = true
                _loginSuccess.value = authRepository.login(_user.value)
                if (_loginSuccess.value != true) {
                    _errorMessage.value = "Email o contraseña incorrectos."
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error de autenticación: ${e.message}"
            }
            finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            clearUser()
        }
    }

    fun register() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _loginSuccess.value = authRepository.register(_user.value)
            } catch (e: Exception) {
                _errorMessage.value = "Error al registrar: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetLoginSuccess() {
        _loginSuccess.value = null
    }

    fun resetErrorMessage() {
        _errorMessage.value = null
    }
}