package pe.edu.upc.vacapp.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColors = lightColorScheme(
    primary = Emerald40,
    onPrimary = Cream95,
    primaryContainer = Emerald90,
    onPrimaryContainer = Emerald30,

    secondary = Sage40,
    onSecondary = Cream95,
    secondaryContainer = Sage90,
    onSecondaryContainer = OnSurfaceLight,

    background = Cream95,
    onBackground = OnSurfaceLight,
    surface = Color.White,
    onSurface = OnSurfaceLight,
    surfaceVariant = Stone90,
    onSurfaceVariant = OnSurfaceVariantLight,
    surfaceContainer = Cream95,
    surfaceContainerHigh = Stone90,
    surfaceContainerHighest = Stone80,

    outline = OutlineLight,
    outlineVariant = Stone80,

    error = Error40,
    onError = Cream95,
    errorContainer = ErrorContainerLight,
    onErrorContainer = OnSurfaceLight
)

@Composable
fun VacAppTheme(
    // The brand is intentionally light-only. We always render with the light
    // scheme, regardless of the system's dark/light setting.
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            dynamicLightColorScheme(context)
        }
        else -> LightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = VacAppShapes,
        content = content
    )
}
