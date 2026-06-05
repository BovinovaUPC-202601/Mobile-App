package pe.edu.upc.vacapp.shared.data.local

import android.content.Context
import android.content.SharedPreferences

/**
 * Lightweight session storage for the logged-in user id and the alert
 * notification bookkeeping (which alert ids were already notified, and whether
 * the seen-set was primed right after login so we don't spam a notification for
 * every pre-existing alert). Mirrors the web frontend's "only notify alerts that
 * arrive after login" behaviour.
 */
object UserStorage {
    private const val PREF_NAME = "prefs"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_SEEN_ALERT_IDS = "seen_alert_ids"
    private const val KEY_ALERTS_PRIMED = "alerts_primed"

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveUserId(userId: Int) {
        prefs.edit().putInt(KEY_USER_ID, userId).apply()
    }

    fun getUserId(): Int = prefs.getInt(KEY_USER_ID, -1)

    fun getSeenAlertIds(): Set<Int> =
        prefs.getStringSet(KEY_SEEN_ALERT_IDS, emptySet())
            ?.mapNotNull { it.toIntOrNull() }
            ?.toSet() ?: emptySet()

    fun saveSeenAlertIds(ids: Set<Int>) {
        prefs.edit().putStringSet(KEY_SEEN_ALERT_IDS, ids.map { it.toString() }.toSet()).apply()
    }

    fun isAlertsPrimed(): Boolean = prefs.getBoolean(KEY_ALERTS_PRIMED, false)

    fun setAlertsPrimed(primed: Boolean) {
        prefs.edit().putBoolean(KEY_ALERTS_PRIMED, primed).apply()
    }

    /** Called on logout so the next user starts clean and re-primes. */
    fun clear() {
        prefs.edit()
            .remove(KEY_USER_ID)
            .remove(KEY_SEEN_ALERT_IDS)
            .remove(KEY_ALERTS_PRIMED)
            .apply()
    }
}
