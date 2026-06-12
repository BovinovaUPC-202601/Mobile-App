package pe.edu.upc.vacapp.iam.presentation.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import pe.edu.upc.vacapp.ui.theme.Emerald40
import pe.edu.upc.vacapp.ui.theme.Emerald90
import pe.edu.upc.vacapp.ui.theme.Error40
import pe.edu.upc.vacapp.ui.theme.ErrorContainerLight

/**
 * Pairs visually with [ErrorState] — a screen can swap between them
 * without layout shift. Used for "no data yet" states (e.g. an empty
 * "Upcoming campaigns" list).
 *
 *  - 80 dp tinted icon disc.
 *  - Title + description stacked below, both centered.
 *  - Optional CTA slot at the bottom — pass a composable lambda (most
 *    often a [PrimaryButton]) when the user can act on the empty state.
 */
@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    cta: (@Composable () -> Unit)? = null
) {
    EmptyErrorState(
        modifier = modifier,
        icon = icon,
        title = title,
        description = description,
        iconBackground = Emerald90,
        iconTint = Emerald40,
        cta = cta
    )
}

/**
 * Same shape as [EmptyState] but tinted with the error palette. The
 * optional `action` slot is independent of `cta` so screens can wire a
 * retry affordance without coupling to [PrimaryButton].
 */
@Composable
fun ErrorState(
    icon: ImageVector,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null
) {
    EmptyErrorState(
        modifier = modifier,
        icon = icon,
        title = title,
        description = description,
        iconBackground = ErrorContainerLight,
        iconTint = Error40,
        cta = action
    )
}

@Composable
private fun EmptyErrorState(
    icon: ImageVector,
    title: String,
    description: String,
    iconBackground: Color,
    iconTint: Color,
    modifier: Modifier = Modifier,
    cta: (@Composable () -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(iconBackground, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(36.dp)
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        if (cta != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                cta()
            }
        }
    }
}
