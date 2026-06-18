package pe.edu.upc.vacapp.iam.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pe.edu.upc.vacapp.iam.data.model.LoginRequest
import pe.edu.upc.vacapp.iam.data.model.RegisterRequest
import pe.edu.upc.vacapp.iam.data.remote.AuthService
import pe.edu.upc.vacapp.iam.domain.model.User
import pe.edu.upc.vacapp.shared.data.local.JwtStorage
import pe.edu.upc.vacapp.shared.session.SessionManager

class AuthRepository(
    private val authService: AuthService
) {
    suspend fun login(user: User): Boolean = withContext(Dispatchers.IO) {
        val res = authService.login(LoginRequest.fromUser(user))

        if (!res.isSuccessful) return@withContext false

        val token = res.body()?.token

        if (token != null) {
            JwtStorage.saveToken(token)
            return@withContext true
        }

        return@withContext false
    }

    suspend fun register(user: User): Boolean = withContext(Dispatchers.IO) {
        val res = authService.register(RegisterRequest.fromUser(user))

        if (!res.isSuccessful) return@withContext false

        val token = res.body()?.token

        if (token != null) {
            JwtStorage.saveToken(token)
            return@withContext true
        }

        return@withContext false
    }

    suspend fun logout(): Boolean = withContext(Dispatchers.IO) {
        // Token + alert bookkeeping + every cached ViewModel, so the next user starts clean.
        SessionManager.logout()

        return@withContext true
    }
}