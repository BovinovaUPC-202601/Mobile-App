package pe.edu.upc.vacapp.alerts.notifications

import android.content.Context
import pe.edu.upc.vacapp.alerts.data.di.DataModule
import pe.edu.upc.vacapp.shared.data.local.JwtStorage
import pe.edu.upc.vacapp.shared.data.local.UserStorage

/**
 * Single source of truth for "fetch alerts and notify the new unread ones".
 * Used both by the foreground 15s poller (while the app is open) and by the
 * background WorkManager job (while the app is closed).
 *
 * Mirrors the web frontend: the first run after login primes the seen-set
 * silently so pre-existing alerts don't all fire a notification; later runs
 * notify only alerts that appeared afterwards.
 */
object AlertNotificationChecker {

    suspend fun checkAndNotify(context: Context) {
        val userId = UserStorage.getUserId()
        if (userId <= 0 || JwtStorage.getToken().isNullOrEmpty()) return

        val alerts = DataModule.repository.getAlertsByUserId(userId)

        // Prime once: remember the current alerts without notifying.
        if (!UserStorage.isAlertsPrimed()) {
            UserStorage.saveSeenAlertIds(alerts.map { it.id }.toSet())
            UserStorage.setAlertsPrimed(true)
            return
        }

        val seen = UserStorage.getSeenAlertIds()
        val newUnread = alerts.filter { it.isUnread && it.id !in seen }
        if (newUnread.isEmpty()) return

        newUnread.forEach { AlertNotifier.notify(context, it) }
        UserStorage.saveSeenAlertIds(seen + newUnread.map { it.id })
    }
}
