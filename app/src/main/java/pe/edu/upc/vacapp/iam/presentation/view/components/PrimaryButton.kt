package pe.edu.upc.vacapp.iam.presentation.view.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pe.edu.upc.vacapp.ui.theme.Cream95
import pe.edu.upc.vacapp.ui.theme.PrimaryButtonGradientEnd
import pe.edu.upc.vacapp.ui.theme.PrimaryButtonGradientStart

/**
 * The flagship button used on auth screens.
 *
 *  - Painted with a brand gradient (not a flat fill) to give a premium feel.
 *  - Subtle elevation that lifts on press, paired with a gentle scale-down.
 *  - Animated label / loading swap via [AnimatedContent] — no jank, no flicker.
 *  - Accessible: exposes [Role.Button] and a configurable [contentDescription].
 */
@Composable
fun PrimaryButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    showTrailingIcon: Boolean = true,
    contentDescription: String = label
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = tween(durationMillis = 140),
        label = "primary-button-press"
    )
    val disabledAlpha = if (enabled) 1f else 0.55f

    val gradient = remember(disabledAlpha) {
        Brush.horizontalGradient(
            colorStops = arrayOf(
                0.0f to PrimaryButtonGradientStart.copy(alpha = disabledAlpha),
                1.0f to PrimaryButtonGradientEnd.copy(alpha = disabledAlpha)
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .scale(pressScale)
            .shadow(
                elevation = if (isPressed) 4.dp else 10.dp,
                shape = RoundedCornerShape(14.dp),
                clip = false,
                ambientColor = PrimaryButtonGradientStart.copy(alpha = 0.25f * disabledAlpha),
                spotColor = PrimaryButtonGradientStart.copy(alpha = 0.30f * disabledAlpha)
            )
            .background(gradient, RoundedCornerShape(14.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled && !isLoading,
                onClick = onClick
            )
            .semantics { role = Role.Button },
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(
            targetState = isLoading,
            transitionSpec = {
                (fadeIn(animationSpec = tween(180)) togetherWith
                    fadeOut(animationSpec = tween(140)))
            },
            label = "primary-button-content"
        ) { loading ->
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color = Cream95,
                    strokeWidth = 2.5.dp
                )
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = label,
                        color = Cream95,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    if (showTrailingIcon) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                            contentDescription = null,
                            tint = Cream95,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}


