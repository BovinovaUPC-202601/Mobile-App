package pe.edu.upc.vacapp.barn.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import pe.edu.upc.vacapp.animal.domain.model.Animal
import pe.edu.upc.vacapp.barn.domain.model.Barn
import pe.edu.upc.vacapp.iam.presentation.view.components.EmptyState
import pe.edu.upc.vacapp.ui.theme.Emerald40
import pe.edu.upc.vacapp.ui.theme.Emerald90

@Composable
fun BarnDetailsView(
    barn: Barn,
    animals: List<Animal>,
    modifier: Modifier = Modifier,
    onAnimalClick: (Animal) -> Unit = {}
) {
    val barnAnimals = animals.filter { it.barnId == barn.id }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = barn.name.ifBlank { "Establo sin nombre" },
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    CapacityBar(
                        used = barnAnimals.size,
                        total = barn.limit.toIntOrNull() ?: 0
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
            if (barnAnimals.isEmpty()) {
                item {
                    EmptyState(
                        icon = Icons.Filled.Pets,
                        title = "Sin animales en este establo",
                        description = "Los animales asignados a este establo aparecerán aquí."
                    )
                }
            } else {
                items(barnAnimals, key = { it.id }) { animal ->
                    AnimalRowCard(
                        animal = animal,
                        onClick = { onAnimalClick(animal) }
                    )
                }
            }
        }
    }
}

@Composable
private fun AnimalRowCard(
    animal: Animal,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                clip = false,
                ambientColor = Color.Black.copy(alpha = 0.04f),
                spotColor = Color.Black.copy(alpha = 0.06f)
            ),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(Emerald90, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Pets,
                    contentDescription = null,
                    tint = Emerald40,
                    modifier = Modifier.size(22.dp)
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = animal.name.ifBlank { "Animal sin nombre" },
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
                Text(
                    text = listOfNotNull(
                        animal.breed.takeIf { it.isNotBlank() },
                        if (animal.age > 0) "${animal.age} m" else null
                    ).joinToString(" · ").ifBlank { "Sin detalles" },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun CapacityBar(
    used: Int,
    total: Int
) {
    val progress = if (total > 0) (used.toFloat() / total).coerceIn(0f, 1f) else 0f

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Capacidad",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "$used/$total · ${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = Emerald40,
            trackColor = Emerald90,
        )
    }
}
