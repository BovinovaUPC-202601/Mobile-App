package pe.edu.upc.vacapp.campaign.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import pe.edu.upc.vacapp.campaign.domain.model.Campaign
import pe.edu.upc.vacapp.campaign.presentation.viewmodel.CampaignViewModel
import pe.edu.upc.vacapp.iam.presentation.view.components.EmptyState

@Composable
fun CampaignView(
    viewModel: CampaignViewModel,
    onTapAddCampaign: () -> Unit = {},
    onEdit: (Campaign) -> Unit = {},
    onDelete: (Int) -> Unit = {},
) {
    val campaigns by viewModel.campaigns.collectAsState()
    var deleteTarget by remember { mutableStateOf<Campaign?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (campaigns.isEmpty()) {
            EmptyState(
                icon = Icons.Filled.MedicalServices,
                title = "Sin campañas aún",
                description = "Las campañas registradas aparecerán aquí."
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(campaigns, key = { it.name + it.startDate.toString() }) { campaign ->
                    CardCampaignView(
                        campaign = campaign,
                        onEdit = onEdit,
                        onDelete = { deleteTarget = it }
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = onTapAddCampaign,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(end = 20.dp, bottom = 20.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Añadir campaña"
            )
        }
    }

    deleteTarget?.let { campaign ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { Text("Eliminar campaña") },
            text = { Text("¿Estás seguro de que deseas eliminar la campaña \"${campaign.name}\"? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    onDelete(campaign.id)
                    deleteTarget = null
                }) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteTarget = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
