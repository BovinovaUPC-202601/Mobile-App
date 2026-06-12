package pe.edu.upc.vacapp.iam.presentation.view.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import pe.edu.upc.vacapp.ui.theme.Emerald30
import pe.edu.upc.vacapp.ui.theme.OnSurfaceVariantLight

/**
 * Lightweight brand wordmark used on auth screens. No logo disc, no shadow —
 * the gradient backdrop already provides visual interest, so the header is
 * just a typographic anchor.
 */
@Composable
fun BrandHeader(
    modifier: Modifier = Modifier,
    brandName: String = "VacApp",
    tagline: String = "Livestock management, modernized.",
    contentPadding: PaddingValues = PaddingValues(horizontal = 24.dp, vertical = 16.dp)
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(contentPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = brandName,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Emerald30
            )
        )
        Text(
            text = tagline,
            style = MaterialTheme.typography.bodyMedium,
            color = OnSurfaceVariantLight,
            textAlign = TextAlign.Center
        )
    }
}
