package pe.edu.upc.vacapp.ui.theme

import androidx.compose.ui.graphics.Color

// ─────────────────────────────────────────────────────────────────────────────
// Brand palette · "Pasture Modern"
// A vibrant, slightly desaturated emerald palette anchored on a single
// primary green with cool sage neutrals. Picked to feel modern and
// trustworthy without veering into neon.
// ─────────────────────────────────────────────────────────────────────────────

// Primary · vibrant emerald
val Emerald40 = Color(0xFF10A065)
val Emerald30 = Color(0xFF0A7E4D)
val Emerald80 = Color(0xFF6FD4A4)
val Emerald90 = Color(0xFFC8F0DA)
val EmeraldContainer = Color(0xFFDCF5E6)

// Secondary · muted sage
val Sage40 = Color(0xFF4A6B5A)
val Sage80 = Color(0xFFB5CFC0)
val Sage90 = Color(0xFFD8E8DD)
val SageContainer = Color(0xFFE4EEE7)

// Neutrals
val Cream95 = Color(0xFFF4F8F2)
val Stone90 = Color(0xFFE1E7DF)
val Stone80 = Color(0xFFC5CCBE)
val OnSurfaceLight = Color(0xFF0E1A12)
val OnSurfaceVariantLight = Color(0xFF4F6354)
val OutlineLight = Color(0xFF7E8F82)

// Error
val Error40 = Color(0xFFD04A3A)
val Error80 = Color(0xFFFFB4A8)
val ErrorContainerLight = Color(0xFFFFD9D2)

// Decorative gradients
val HeroGradientStart = Color(0xFFB8E4CB)
val HeroGradientMid = Color(0xFFDCF1DE)
val HeroGradientEnd = Color(0xFFF4F8F2)

val PrimaryButtonGradientStart = Color(0xFF10A065)
val PrimaryButtonGradientEnd = Color(0xFF0A7E4D)

// ─────────────────────────────────────────────────────────────────────────────
// Backwards-compat custom color aliases.
// The pre-rework codebase used `pe.edu.upc.vacapp.ui.theme.Color.*` directly.
// We keep the type and the public surface, but re-point the values to the
// new palette so unrelated screens don't visually break.
// ─────────────────────────────────────────────────────────────────────────────
object Color {
    val ForestGreen = Color(0xFF10A065)
    val LightGray = Color(0xFFF2F2F2)
    val AlmondCream = Color(0xFFE4EEE7)
    val Green = Color(0xFF4A6B5A)
    val Black = Color(0xFF0E1A12)
    val White = Color(0xFFFFFFFF)
    val Transparent = Color.Transparent
}
