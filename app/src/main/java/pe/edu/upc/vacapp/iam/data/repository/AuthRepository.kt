package pe.edu.upc.vacapp.iam.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pe.edu.upc.vacapp.iam.data.model.LoginRequest
import pe.edu.upc.vacapp.iam.data.model.RegisterRequest
import pe.edu.upc.vacapp.iam.data.remote.AuthService
import pe.edu.upc.vacapp.iam.domain.model.User
import pe.edu.upc.vacapp.shared.data.local.JwtStorage
import pe.edu.upc.vacapp.shared.data.local.UserStorage

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
        JwtStorage.clearToken()
        // Reset notification bookkeeping so the next user re-primes from scratch.
        UserStorage.clear()

        return@withContext true
    }
}