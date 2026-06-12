package pe.edu.upc.vacapp.iam.presentation.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pe.edu.upc.vacapp.ui.theme.Emerald40
import pe.edu.upc.vacapp.ui.theme.Emerald90
import pe.edu.upc.vacapp.ui.theme.Sand40
import pe.edu.upc.vacapp.ui.theme.Sand90
import pe.edu.upc.vacapp.ui.theme.Sky40
import pe.edu.upc.vacapp.ui.theme.Sky90

/**
 * Accent variant for [StatCard]. Drives the icon disc background and the
 * icon tint. The card surface itself stays neutral so the three variants
 * can sit side by side without competing.
 */
enum class StatCardAccent { Emerald, Sand, Sky }

private data class AccentPalette(val background: Color, val foreground: Color)

private fun StatCardAccent.palette(): AccentPalette = when (this) {
    StatCardAccent.Emerald -> AccentPalette(Emerald90, Emerald40)
    StatCardAccent.Sand -> AccentPalette(Sand90, Sand40)
    StatCardAccent.Sky -> AccentPalette(Sky90, Sky40)
}

/**
 * Dashboard stat card: a tinted icon disc, a large bold number, and a
 * label underneath. Used on the home dashboard to differentiate the
 * three at-a-glance metrics (animals, campaigns, barns) without breaking
 * the brand.
 *
 *  - 16 dp corner radius, 6 dp shadow.
 *  - Clickable; expose [Role.Button] for accessibility.
 *  - Pulls the body text colors from [MaterialTheme.colorScheme] so it
 *    tracks the rest of the app automatically.
 */
@Composable
fun StatCard(
    label: String,
    value: String,
    icon: ImageVector,
    accent: StatCardAccent,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val palette = accent.palette()
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(16.dp),
                clip = false,
                ambientColor = Color.Black.copy(alpha = 0.05f),
                spotColor = Color.Black.copy(alpha = 0.08f)
            )
            .clickable(onClick = onClick)
            .semantics { role = Role.Button },
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(palette.background, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = palette.foreground,
                    modifier = Modifier.size(22.dp)
                )
            }
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
