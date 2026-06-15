package pe.edu.upc.vacapp.alerts.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import pe.edu.upc.vacapp.MainActivity
import pe.edu.upc.vacapp.R
import pe.edu.upc.vacapp.alerts.domain.model.Alert

/**
 * Builds and shows system notifications for biometric alerts. The on-screen
 * equivalent of the web frontend's corner toasts.
 */
object AlertNotifier {
    private const val CHANNEL_ID = "vacapp_alerts"
    private const val CHANNEL_NAME = "Alertas biométricas"

    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Avisos cuando un collar reporta signos vitales fuera de rango"
            }
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    fun notify(context: Context, alert: Alert) {
        // Android 13+ requires the runtime POST_NOTIFICATIONS permission.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ActivityCompat.checkSelfPermission(context, "android.permission.POST_NOTIFICATIONS")
            != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val tapIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            alert.id,
            tapIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Account-level alerts (e.g. collar-return) have no bovine — label by category instead.
        val scope = if (alert.isAccountLevel) alert.alertTypeLabel else "Bovino ${alert.bovineId}"
        val title = when {
            alert.isRed    -> "🔴 Alerta crítica · $scope"
            alert.isYellow -> "🟡 Alerta · $scope"
            else           -> "Alerta · $scope"
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(alert.message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(alert.message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        NotificationManagerCompat.from(context).notify(alert.id, notification)
    }
}
