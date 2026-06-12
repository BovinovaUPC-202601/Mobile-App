package pe.edu.upc.vacapp.iam.presentation.view.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import pe.edu.upc.vacapp.ui.theme.HeroGradientEnd
import pe.edu.upc.vacapp.ui.theme.HeroGradientMid
import pe.edu.upc.vacapp.ui.theme.HeroGradientStart
import pe.edu.upc.vacapp.ui.theme.Sage90

/**
 * Soft, light-only backdrop for the Login screen.
 *
 * A vertical gradient (sage → cream) anchors the top of the screen and two
 * extra-faded blob accents add visual interest without ever competing with
 * the form. Stateless and theme-agnostic: the parent decides what to layer
 * on top.
 */
@Composable
fun LoginBackdrop(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colorStops = arrayOf(
                        0.00f to HeroGradientStart,
                        0.45f to HeroGradientMid,
                        1.00f to HeroGradientEnd
                    )
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawSoftBlob(
                color = Sage90.copy(alpha = 0.65f),
                center = Offset(size.width * 0.15f, size.height * 0.18f),
                radius = size.minDimension * 0.55f
            )
            drawSoftBlob(
                color = HeroGradientStart.copy(alpha = 0.45f),
                center = Offset(size.width * 0.95f, size.height * 0.35f),
                radius = size.minDimension * 0.40f
            )
            drawSoftBlob(
                color = Sage90.copy(alpha = 0.30f),
                center = Offset(size.width * 0.55f, size.height * 1.02f),
                radius = size.minDimension * 0.55f
            )
        }
    }
}

private fun DrawScope.drawSoftBlob(
    color: Color,
    center: Offset,
    radius: Float
) {
    drawCircle(
        color = color,
        radius = radius,
        center = center
    )
    drawCircle(
        color = color.copy(alpha = color.alpha * 0.45f),
        radius = radius * 0.65f,
        center = center
    )
}
