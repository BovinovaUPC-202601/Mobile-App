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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.edu.upc.vacapp.campaign.presentation.viewmodel.CampaignViewModel
import pe.edu.upc.vacapp.iam.presentation.view.components.EmptyState

@Composable
fun CampaignView(
    viewModel: CampaignViewModel,
    onTapAddCampaign: () -> Unit = {}
) {
    val campaigns by viewModel.campaigns.collectAsState()

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
                    CardCampaignView(campaign = campaign)
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
}
