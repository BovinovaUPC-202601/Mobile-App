package pe.edu.upc.vacapp.monitoring.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.edu.upc.vacapp.monitoring.domain.model.HealthRecord
import pe.edu.upc.vacapp.monitoring.presentation.viewmodel.MonitoringViewModel
import pe.edu.upc.vacapp.ui.theme.Color

@Composable
fun MonitoringView(
    viewModel: MonitoringViewModel,
    // Bovines that have a collar assigned — the only ones that stream IoT data.
    bovines: List<Pair<Int, String>> = emptyList()
) {
    val latest  by viewModel.latest.collectAsState()
    val history by viewModel.history.collectAsState()
    val loading by viewModel.loading.collectAsState()

    var selectedId by remember { mutableStateOf<Int?>(null) }
    var menuOpen by remember { mutableStateOf(false) }

    // Auto-select the first available bovine and load it — no manual ID search needed.
    LaunchedEffect(bovines) {
        if (selectedId == null && bovines.isNotEmpty()) {
            val first = bovines.first().first
            selectedId = first
            viewModel.loadData(first)
        }
    }

    val selectedName = bovines.firstOrNull { it.first == selectedId }?.second

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Monitoreo IoT", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Green)

        if (bovines.isEmpty()) {
            Text(
                "No tenés bovinos con collar asignado. Asigná un collar para monitorear.",
                fontSize = 14.sp,
                color = androidx.compose.ui.graphics.Color.Gray
            )
        } else {
            // Bovine selector — only collared bovines, ready to pick on entry.
            Box {
                OutlinedButton(onClick = { menuOpen = true }, modifier = Modifier.fillMaxWidth()) {
                    Text(selectedName ?: "Seleccioná un bovino", modifier = Modifier.weight(1f))
                    Text("▾")
                }
                DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                    bovines.forEach { (id, name) ->
                        DropdownMenuItem(
                            text = { Text(name) },
                            onClick = {
                                menuOpen = false
                                selectedId = id
                                viewModel.loadData(id)
                            }
                        )
                    }
                }
            }
        }

        if (loading) {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color.Green)
            }
        }

        // Latest record
        latest?.let { record ->
            HealthRecordCard(record = record, isLatest = true)
        }

        if (!loading && selectedId != null && latest == null) {
            Text(
                "Sin lecturas registradas para este bovino todavía.",
                fontSize = 13.sp,
                color = androidx.compose.ui.graphics.Color.Gray
            )
        }

        // History
        if (history.isNotEmpty()) {
            Text("Historial", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.Green)
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(history) { record ->
                    HealthRecordCard(record = record, isLatest = false)
                }
            }
        }
    }
}

@Composable
fun HealthRecordCard(record: HealthRecord, isLatest: Boolean) {
    val cardColor = if (record.isAlert)
        androidx.compose.ui.graphics.Color(0xFFFFEBEE)
    else
        androidx.compose.ui.graphics.Color(0xFFE8F5E9)

    val borderColor = if (record.isAlert)
        androidx.compose.ui.graphics.Color(0xFFEF9A9A)
    else
        androidx.compose.ui.graphics.Color(0xFFA5D6A7)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(borderColor)
        )
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isLatest) "Última lectura" else "Registro #${record.id}",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                Text(
                    text = if (record.isAlert) "⚠ ALERTA" else "✓ Normal",
                    fontSize = 12.sp,
                    color = if (record.isAlert)
                        androidx.compose.ui.graphics.Color(0xFFD32F2F)
                    else
                        androidx.compose.ui.graphics.Color(0xFF388E3C),
                    fontWeight = FontWeight.Bold
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("🌡 ${record.temperature} °C", fontSize = 14.sp)
                Text("❤ ${record.heartRate.toInt()} BPM", fontSize = 14.sp)
                Text("🔋 ${record.batteryLevel}%", fontSize = 14.sp)
            }

            Text(
                text = "Dispositivo: ${record.deviceId}",
                fontSize = 11.sp,
                color = androidx.compose.ui.graphics.Color.Gray
            )
            Text(
                text = record.recordedAt,
                fontSize = 11.sp,
                color = androidx.compose.ui.graphics.Color.Gray
            )
        }
    }
}
