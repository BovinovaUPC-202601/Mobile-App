package pe.edu.upc.vacapp.animal.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import pe.edu.upc.vacapp.animal.domain.model.Animal
import pe.edu.upc.vacapp.animal.domain.model.AnimalImage
import pe.edu.upc.vacapp.animal.presentation.viewmodel.AnimalViewModel
import pe.edu.upc.vacapp.iam.presentation.view.components.EmptyState
import pe.edu.upc.vacapp.ui.theme.Emerald40
import pe.edu.upc.vacapp.ui.theme.Emerald90
import pe.edu.upc.vacapp.ui.theme.Sky40
import pe.edu.upc.vacapp.ui.theme.Sky90

@Composable
fun AnimalCardList(
    viewmodel: AnimalViewModel,
    onTap: (Animal) -> Unit,
    onTapAddAnimal: () -> Unit = {}
) {
    val animals by viewmodel.animals.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (animals.isEmpty()) {
            EmptyState(
                icon = Icons.Filled.Pets,
                title = "Sin animales aún",
                description = "Los animales registrados aparecerán aquí."
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(animals, key = { it.id }) { animal ->
                    AnimalCard(
                        animal = animal,
                        onClick = { onTap(animal) }
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = onTapAddAnimal,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(end = 20.dp, bottom = 20.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Añadir animal"
            )
        }
    }
}

@Composable
fun AnimalCard(
    animal: Animal,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val imgUrl = when (val image = animal.image) {
        is AnimalImage.FromUrl -> image.url
        else -> null
    }

    Surface(
        onClick = onClick,
        modifier = modifier
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
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (imgUrl != null) {
                AsyncImage(
                    model = imgUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(Emerald90, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Pets,
                        contentDescription = null,
                        tint = Emerald40,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = animal.name.ifBlank { "Animal sin nombre" },
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    GenderMiniBadge(isMale = animal.isMale)
                }
                Text(
                    text = listOfNotNull(
                        animal.breed.takeIf { it.isNotBlank() },
                        if (animal.age > 0) "${animal.age} m" else null,
                        animal.barnName.takeIf { it.isNotBlank() }
                    ).joinToString(" · ").ifBlank { "Sin detalles" },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
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
private fun GenderMiniBadge(isMale: Boolean) {
    Box(
        modifier = Modifier
            .size(24.dp)
            .background(
                if (isMale) Emerald90 else Sky90,
                RoundedCornerShape(6.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (isMale) "\u2642" else "\u2640",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = if (isMale) Emerald40 else Sky40
        )
    }
}
