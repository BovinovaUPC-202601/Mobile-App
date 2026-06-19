package pe.edu.upc.vacapp.home.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Warehouse
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pe.edu.upc.vacapp.home.presentation.viewmodel.HomeViewModel
import pe.edu.upc.vacapp.iam.presentation.view.components.EmptyState
import pe.edu.upc.vacapp.iam.presentation.view.components.StatCard
import pe.edu.upc.vacapp.iam.presentation.view.components.StatCardAccent
import pe.edu.upc.vacapp.iam.presentation.view.components.UpcomingEventCard
import pe.edu.upc.vacapp.shared.data.di.timeFormatter

@Composable
fun HomeView(
    onTapAddCampaign: () -> Unit = {},
    onTapAnimal: () -> Unit = {},
    onTapAddBarn: () -> Unit = {},
    onTapAddProduct: () -> Unit = {},
    onTapAddCategory: () -> Unit = {},
    onTapInventory: () -> Unit = {},
    onTapAnimalsSection: () -> Unit = {},
    onTapCampaignSection: () -> Unit = {},
    onTapBarnSection: () -> Unit = {},
    onTapInventorySection: () -> Unit = {},
    viewmodel: HomeViewModel
) {
    val userInfo by viewmodel.userInfo.collectAsState()
    val errorMessage by viewmodel.errorMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var fabExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(errorMessage) {
        errorMessage?.let { snackbarHostState.showSnackbar(it) }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = maxHeight)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Hola, ${userInfo.name.ifBlank { "ahí" }}",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Así está tu rancho hoy.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                StatCard(
                    label = "Animales registrados",
                    value = userInfo.totalAnimals.toString(),
                    icon = Icons.Filled.Pets,
                    accent = StatCardAccent.Emerald,
                    onClick = onTapAnimalsSection
                )

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        StatCard(
                            label = "Campañas",
                            value = userInfo.totalCampaigns.toString(),
                            icon = Icons.Filled.MedicalServices,
                            accent = StatCardAccent.Sand,
                            onClick = onTapCampaignSection
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        StatCard(
                            label = "Establos",
                            value = userInfo.totalBarns.toString(),
                            icon = Icons.Filled.Warehouse,
                            accent = StatCardAccent.Sky,
                            onClick = onTapBarnSection
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Próximas campañas",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (userInfo.nextCampaigns.isEmpty()) {
                        EmptyState(
                            icon = Icons.Filled.CalendarMonth,
                            title = "Nada programado",
                            description = "No tienes campañas próximas. Toca + para crear una."
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            userInfo.nextCampaigns.forEach { campaign ->
                                UpcomingEventCard(
                                    title = campaign.name,
                                    dateRange = "${timeFormatter.format(campaign.startDate)} / ${timeFormatter.format(campaign.endDate)}",
                                    icon = Icons.Filled.MedicalServices,
                                    onClick = onTapCampaignSection
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(end = 25.dp, bottom = 0.dp)
        ) {
            FloatingActionButton(
                onClick = { fabExpanded = !fabExpanded },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = if (fabExpanded) Icons.Filled.Close else Icons.Filled.Add,
                    contentDescription = if (fabExpanded) "Cerrar menú" else "Añadir"
                )
            }
            DropdownMenu(
                expanded = fabExpanded,
                onDismissRequest = { fabExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Animal") },
                    leadingIcon = {
                        Icon(Icons.Filled.Pets, contentDescription = null)
                    },
                    onClick = {
                        fabExpanded = false
                        onTapAnimal()
                    }
                )
                DropdownMenuItem(
                    text = { Text("Campaña") },
                    leadingIcon = {
                        Icon(Icons.Filled.MedicalServices, contentDescription = null)
                    },
                    onClick = {
                        fabExpanded = false
                        onTapAddCampaign()
                    }
                )
                DropdownMenuItem(
                    text = { Text("Establo") },
                    leadingIcon = {
                        Icon(Icons.Filled.Warehouse, contentDescription = null)
                    },
                    onClick = {
                        fabExpanded = false
                        onTapAddBarn()
                    }
                )
                DropdownMenuItem(
                    text = { Text("Producto") },
                    leadingIcon = {
                        Icon(Icons.Filled.Inventory2, contentDescription = null)
                    },
                    onClick = {
                        fabExpanded = false
                        onTapAddProduct()
                    }
                )
                DropdownMenuItem(
                    text = { Text("Categoría") },
                    leadingIcon = {
                        Icon(Icons.Filled.Folder, contentDescription = null)
                    },
                    onClick = {
                        fabExpanded = false
                        onTapAddCategory()
                    }
                )
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 96.dp)
        )
    }
}
