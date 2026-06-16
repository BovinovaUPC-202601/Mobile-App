package pe.edu.upc.vacapp.iam.presentation.view.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications

/**
 * Top bar used across post-login screens.
 *
 *  - Hamburger on the left to open the navigation drawer.
 *  - Stacked greeting ("Hello, X") and optional page title in the middle.
 *  - Notification bell on the right.
 *  - White surface, 56 dp tall, with a 1 dp bottom shadow so it reads as
 *    elevated above the screen content (matching the auth screen cards).
 */
@Composable
fun AppTopBar(
    greeting: String,
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    onBellClick: () -> Unit = {}
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Abrir menú",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = greeting,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (title != null) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            IconButton(onClick = onBellClick) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notificaciones",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
