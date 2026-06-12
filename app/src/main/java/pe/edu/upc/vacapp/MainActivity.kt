package pe.edu.upc.vacapp

import android.graphics.Color as AndroidColor
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import pe.edu.upc.vacapp.alerts.notifications.AlertNotificationChecker
import pe.edu.upc.vacapp.iam.presentation.navigation.NavigationAuth
import pe.edu.upc.vacapp.ui.theme.VacAppTheme

class MainActivity : ComponentActivity() {

    private val requestNotificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { /* result ignored */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // The brand is light-only. Both system bars are forced to a solid
        // black scrim with light icons so the Login / Register screens read
        // with a deliberate dark frame at top and bottom.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(AndroidColor.BLACK, AndroidColor.BLACK),
            navigationBarStyle = SystemBarStyle.dark(AndroidColor.BLACK)
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermission.launch("android.permission.POST_NOTIFICATIONS")
        }

        // Foreground poller: while the app is visible, check for new alerts every
        // 15s and raise notifications — the mobile equivalent of the web toasts.
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                while (isActive) {
                    AlertNotificationChecker.checkAndNotify(applicationContext)
                    delay(POLL_INTERVAL_MS)
                }
            }
        }

        setContent {
            VacAppTheme {
                NavigationAuth()
            }
        }
    }

    companion object {
        private const val POLL_INTERVAL_MS = 15_000L
    }
}
