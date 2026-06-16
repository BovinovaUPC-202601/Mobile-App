package pe.edu.upc.vacapp.barn.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Warehouse
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.edu.upc.vacapp.animal.domain.model.Animal
import pe.edu.upc.vacapp.barn.domain.model.Barn
import pe.edu.upc.vacapp.barn.presentation.viewmodel.BarnViewModel
import pe.edu.upc.vacapp.iam.presentation.view.components.EmptyState

@Composable
fun BarnView(
    viewModel: BarnViewModel,
    animals: List<Animal> = emptyList(),
    onTapAddBarn: () -> Unit = {},
    onBarnClick: (Barn) -> Unit = {}
) {
    val barns by viewModel.barn.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (barns.isEmpty()) {
            EmptyState(
                icon = Icons.Filled.Warehouse,
                title = "Sin establos aún",
                description = "No has registrado ningún establo. Toca + para crear el primero."
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(barns, key = { it.id }) { barn ->
                    BarnCardView(
                        barn = barn,
                        barnAnimals = animals.count { it.barnId == barn.id },
                        onClick = { onBarnClick(barn) }
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = onTapAddBarn,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(end = 20.dp, bottom = 20.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Añadir establo"
            )
        }
    }
}
