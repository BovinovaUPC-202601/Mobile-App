package pe.edu.upc.vacapp.iam.presentation.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import pe.edu.upc.vacapp.ui.theme.Cream95
import pe.edu.upc.vacapp.ui.theme.PrimaryButtonGradientEnd
import pe.edu.upc.vacapp.ui.theme.PrimaryButtonGradientStart

/**
 * Single navigation entry in [AppDrawer]. Kept as a plain data class so the
 * caller can build the list at the call site (Navigation.kt) without
 * coupling the component to a specific route table.
 */
data class DrawerItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
    // Plus-only features (IA, IoT monitoring) — hidden from the drawer when the user is Free.
    val plusOnly: Boolean = false
)

/**
 * Side navigation drawer used across post-login screens.
 *
 *  - White surface with an emerald gradient header (initials disc + name).
 *    The header is intentionally low on data: the backend doesn't expose
 *    the user's email through the current `userInfo` endpoint, so the
 *    name is the only reliable handle we have until that changes.
 *  - Navigation items render as rounded pills; the active route gets the
 *    `primaryContainer` background, a colored icon, and a SemiBold label
 *    so the user can tell at a glance where they are.
 *  - Footer: a thin divider and a muted "Log out" row. The sign-out
 *    action is passed in by the caller (Navigation.kt wires the actual
 *    clear + navigation flow).
 */
@Composable
fun AppDrawer(
    userName: String,
    items: List<DrawerItem>,
    activeRoute: String?,
    onItemClick: (DrawerItem) -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier,
    plan: String = "Free"
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        DrawerHeader(userName = userName, plan = plan)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items.forEach { item ->
                DrawerItemRow(
                    item = item,
                    isActive = item.route == activeRoute,
                    onClick = { onItemClick(item) }
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )
        DrawerFooter(onSignOut = onSignOut)
    }
}

@Composable
private fun DrawerHeader(userName: String, plan: String) {
    val initials = remember(userName) { computeInitials(userName) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        PrimaryButtonGradientStart,
                        PrimaryButtonGradientEnd
                    )
                )
            )
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Cream95.copy(alpha = 0.18f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials,
                    style = MaterialTheme.typography.titleMedium,
                    color = Cream95,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Text(
                text = userName.ifBlank { "Usuario" },
                style = MaterialTheme.typography.titleMedium,
                color = Cream95,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            PlanBadge(plan = plan)
        }
    }
}

@Composable
private fun PlanBadge(plan: String) {
    val isPlus = plan.equals("Plus", ignoreCase = true)
    val bg = if (isPlus) Color(0xFFFCD34D) else Cream95.copy(alpha = 0.20f)
    val fg = if (isPlus) Color(0xFF78350F) else Cream95
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(percent = 50))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            text = if (isPlus) "Plus" else "Gratis",
            style = MaterialTheme.typography.labelSmall,
            color = fg,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun DrawerItemRow(
    item: DrawerItem,
    isActive: Boolean,
    onClick: () -> Unit
) {
    val background = if (isActive) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        Color.Transparent
    }
    val foreground = if (isActive) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    val labelWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .clip(RoundedCornerShape(percent = 50))
            .background(background)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = null,
            tint = foreground,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = item.label,
            style = MaterialTheme.typography.bodyLarge,
            color = foreground,
            fontWeight = labelWeight,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun DrawerFooter(onSignOut: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSignOut)
            .padding(horizontal = 28.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.Logout,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = "Cerrar sesión",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun computeInitials(name: String): String {
    val trimmed = name.trim()
    if (trimmed.isEmpty()) return "?"
    val parts = trimmed.split(Regex("\\s+"))
    return when {
        parts.size >= 2 -> "${parts[0].first().uppercase()}${parts[1].first().uppercase()}"
        else -> trimmed.take(2).uppercase()
    }
}
