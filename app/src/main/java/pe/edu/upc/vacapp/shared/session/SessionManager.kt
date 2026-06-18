package pe.edu.upc.vacapp.shared.session

import pe.edu.upc.vacapp.shared.data.local.JwtStorage
import pe.edu.upc.vacapp.shared.data.local.UserStorage

/**
 * Single entry point for ending a session. Clears the auth token, the alert
 * notification bookkeeping and every cached feature ViewModel, so signing in as
 * a different user always starts from a clean slate. Both logout paths (the
 * drawer and [pe.edu.upc.vacapp.iam.data.repository.AuthRepository]) funnel
 * through here so they stay consistent.
 */
object SessionManager {
    fun logout() {
        JwtStorage.clearToken()
        UserStorage.clear()
        SessionScope.reset()
    }
}
