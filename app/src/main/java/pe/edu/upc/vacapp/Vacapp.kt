package pe.edu.upc.vacapp

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import pe.edu.upc.vacapp.alerts.notifications.AlertNotificationScheduler
import pe.edu.upc.vacapp.alerts.notifications.AlertNotifier
import pe.edu.upc.vacapp.shared.data.local.JwtStorage
import pe.edu.upc.vacapp.shared.data.local.UserStorage

class Vacapp : Application() {
    companion object {
        lateinit var instance: Vacapp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        AndroidThreeTen.init(this)
        JwtStorage.init(applicationContext)
        UserStorage.init(applicationContext)
        AlertNotifier.createChannel(applicationContext)
        // Background poll for alerts while the app is closed. The worker no-ops
        // until a user is logged in (no userId / token => returns early).
        AlertNotificationScheduler.schedule(applicationContext)
    }
}
