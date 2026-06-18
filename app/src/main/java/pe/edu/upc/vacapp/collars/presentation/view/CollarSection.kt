package pe.edu.upc.vacapp.collars.presentation.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pe.edu.upc.vacapp.collars.domain.model.CollarId
import pe.edu.upc.vacapp.collars.presentation.viewmodel.CollarViewModel
import pe.edu.upc.vacapp.iam.presentation.view.components.PrimaryButton
import pe.edu.upc.vacapp.ui.theme.OnSurfaceVariantLight

/**
 * Per-bovine IoT collar management, mirroring the web client's CollarSection.
 * Plus-only: when the backend gates collars (403) a hint is shown instead.
 */
@Composable
fun CollarSection(
    bovineId: Int,
    viewModel: CollarViewModel
) {
    val collars by viewModel.collars.collectAsState()
    val capacity by viewModel.capacity.collectAsState()
    val requiresPlus by viewModel.requiresPlus.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    val collar = collars.firstOrNull { it.bovineId == bovineId }
    val available = remember(collars, capacity) { viewModel.availableNumbers() }
    val clipboard = LocalClipboardManager.current

    var changing by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf<Int?>(null) }
    var menuOpen by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Collar IoT",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                )
                if (!requiresPlus) {
                    Text(
                        text = "${capacity.available}/${capacity.allowance} disponibles",
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceVariantLight
                    )
                }
            }

            when {
                requiresPlus -> Text(
                    text = "Los collares IoT están disponibles en el plan Plus.",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariantLight
                )

                collar != null && !changing -> Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = CollarId.label(collar.deviceId),
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                            )
                            collar.operationalStatus?.let {
                                Text(it, style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariantLight)
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            TextButton(onClick = { changing = true; selected = null }, enabled = !loading) {
                                Text("Cambiar")
                            }
                            TextButton(onClick = { viewModel.remove(collar.id) }, enabled = !loading) {
                                Text("Quitar", color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                    // Device id to flash into the ESP32 — stays visible while a collar is
                    // assigned, so the rancher can copy it any time, not only right after registering.
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Copiá este ID a tu ESP32 (DEVICE_ID):",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = OnSurfaceVariantLight
                                )
                                Text(
                                    text = collar.deviceId,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                                )
                            }
                            TextButton(onClick = { clipboard.setText(AnnotatedString(collar.deviceId)) }) {
                                Text("Copiar")
                            }
                        }
                    }
                }

                else -> {
                    val noCapacity = collar == null && available.isEmpty()
                    if (noCapacity) {
                        Text(
                            text = "Sin collares disponibles. Solicitá uno adicional en Suscripción.",
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceVariantLight
                        )
                    } else {
                        OutlinedButton(onClick = { menuOpen = true }, modifier = Modifier.fillMaxWidth()) {
                            Text(selected?.let { "Collar $it" } ?: "Elegí un collar")
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }
                        DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                            available.forEach { n ->
                                DropdownMenuItem(
                                    text = { Text("Collar $n") },
                                    onClick = { selected = n; menuOpen = false }
                                )
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            PrimaryButton(
                                label = if (collar != null) "Confirmar cambio" else "Asignar",
                                onClick = {
                                    val n = selected ?: return@PrimaryButton
                                    if (collar != null) viewModel.change(n, bovineId)
                                    else viewModel.assign(n, bovineId)
                                    changing = false
                                    selected = null
                                },
                                isLoading = loading,
                                enabled = selected != null && !loading,
                                showTrailingIcon = false,
                                modifier = Modifier.weight(1f)
                            )
                            if (changing) {
                                TextButton(onClick = { changing = false; selected = null }) {
                                    Text("Cancelar")
                                }
                            }
                        }
                    }
                }
            }

            error?.let {
                Text("⚠️ $it", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
