package pe.edu.upc.vacapp.alerts.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

/**
 * Background job that polls for new alerts while the app is closed and raises
 * system notifications for them. Scheduled periodically (see
 * [AlertNotificationScheduler]); the OS enforces a 15-minute minimum interval.
 */
class AlertPollWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            AlertNotificationChecker.checkAndNotify(applicationContext)
            Result.success()
        } catch (_: Exception) {
            // Transient failure (no network, backend down) — let WorkManager retry.
            Result.retry()
        }
    }
}
