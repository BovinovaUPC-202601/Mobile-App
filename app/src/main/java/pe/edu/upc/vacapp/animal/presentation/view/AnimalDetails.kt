package pe.edu.upc.vacapp.animal.presentation.view

import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pets
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.ui.text.input.KeyboardType
import pe.edu.upc.vacapp.animal.presentation.viewmodel.AnimalViewModel
import pe.edu.upc.vacapp.collars.presentation.view.CollarSection
import pe.edu.upc.vacapp.collars.presentation.viewmodel.CollarViewModel
import pe.edu.upc.vacapp.iam.presentation.view.components.PrimaryButton
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
import pe.edu.upc.vacapp.ui.theme.Emerald40
import pe.edu.upc.vacapp.ui.theme.Emerald90
import pe.edu.upc.vacapp.ui.theme.Sky40
import pe.edu.upc.vacapp.ui.theme.Sky90

@Composable
fun AnimalDetails(
    animal: Animal,
    collarViewModel: CollarViewModel? = null,
    animalViewModel: AnimalViewModel? = null
) {
    LaunchedEffect(animal.id) {
        collarViewModel?.fetchCollars()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 480.dp)
                    .shadow(
                        elevation = 12.dp,
                        shape = RoundedCornerShape(20.dp),
                        clip = false,
                        ambientColor = Color.Black.copy(alpha = 0.04f),
                        spotColor = Color.Black.copy(alpha = 0.06f)
                    ),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 0.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Header: name + gender
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
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
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = animal.name.ifBlank { "Unnamed animal" },
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (animal.breed.isNotBlank()) {
                                Text(
                                    text = animal.breed,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        GenderBadge(isMale = animal.isMale)
                    }

                    // Image (if available from URL)
                    val imgUrl = when (val image = animal.image) {
                        is AnimalImage.FromUrl -> image.url
                        else -> null
                    }
                    if (imgUrl != null) {
                        AsyncImage(
                            model = imgUrl,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                    // Details grid
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        DetailRow(label = "Birthdate", value = animal.birthDate.ifBlank { "—" })
                        DetailRow(label = "Barn", value = animal.barnName.ifBlank { "—" })
                        DetailRow(
                            label = "Age",
                            value = if (animal.age > 0) "${animal.age} months" else "—"
                        )
                        ThresholdSection(animal = animal, animalViewModel = animalViewModel)
                    }

                    collarViewModel?.let { vm ->
                        CollarSection(bovineId = animal.id, viewModel = vm)
                    }
                }
            }
        }
    }
}

@Composable
private fun GenderBadge(isMale: Boolean) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .background(
                if (isMale) Emerald90 else Sky90,
                RoundedCornerShape(10.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (isMale) "\u2642" else "\u2640",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = if (isMale) Emerald40 else Sky40
        )
    }
}

/**
 * Per-bovine biometric thresholds. Read-only by default; when an [animalViewModel]
 * is supplied (the IoT/monitoring flow) an "Editar umbrales" action turns the four
 * values into editable fields and persists them via PUT /bovines/{id}, mirroring
 * the web client. These thresholds drive the biometric alerts.
 */
@Composable
private fun ThresholdSection(
    animal: Animal,
    animalViewModel: AnimalViewModel?
) {
    // Reflect server-confirmed edits; seed from the animal passed in.
    val updated by (animalViewModel?.updatedAnimal ?: kotlinx.coroutines.flow.MutableStateFlow(null))
        .collectAsState(null)
    val current = updated?.takeIf { it.id == animal.id } ?: animal
    val isLoading by (animalViewModel?.isLoading ?: kotlinx.coroutines.flow.MutableStateFlow(false))
        .collectAsState(false)

    var editing by remember(animal.id) { mutableStateOf(false) }
    var minTemp by remember(animal.id) { mutableStateOf(animal.minTemperature.toString()) }
    var maxTemp by remember(animal.id) { mutableStateOf(animal.maxTemperature.toString()) }
    var minHr by remember(animal.id) { mutableStateOf(animal.minHeartRate.toString()) }
    var maxHr by remember(animal.id) { mutableStateOf(animal.maxHeartRate.toString()) }

    if (!editing) {
        DetailRow(
            label = "Temperature range",
            value = "${current.minTemperature} – ${current.maxTemperature} °C"
        )
        DetailRow(
            label = "Heart rate range",
            value = "${current.minHeartRate} – ${current.maxHeartRate} BPM"
        )
        if (animalViewModel != null) {
            TextButton(onClick = {
                minTemp = current.minTemperature.toString()
                maxTemp = current.maxTemperature.toString()
                minHr = current.minHeartRate.toString()
                maxHr = current.maxHeartRate.toString()
                editing = true
            }) { Text("Editar umbrales") }
        }
        return
    }

    Text(
        text = "Editar umbrales",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        ThresholdField("Temp. mín (°C)", minTemp, Modifier.weight(1f)) { minTemp = it }
        ThresholdField("Temp. máx (°C)", maxTemp, Modifier.weight(1f)) { maxTemp = it }
    }
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        ThresholdField("Pulso mín (BPM)", minHr, Modifier.weight(1f)) { minHr = it }
        ThresholdField("Pulso máx (BPM)", maxHr, Modifier.weight(1f)) { maxHr = it }
    }
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        PrimaryButton(
            label = "Guardar umbrales",
            onClick = {
                animalViewModel?.updateAnimal(
                    animal.copy(
                        minTemperature = minTemp.toDoubleOrNull() ?: animal.minTemperature,
                        maxTemperature = maxTemp.toDoubleOrNull() ?: animal.maxTemperature,
                        minHeartRate = minHr.toIntOrNull() ?: animal.minHeartRate,
                        maxHeartRate = maxHr.toIntOrNull() ?: animal.maxHeartRate
                    )
                )
                editing = false
            },
            isLoading = isLoading,
            showTrailingIcon = false,
            modifier = Modifier.weight(1f)
        )
        TextButton(onClick = { editing = false }) { Text("Cancelar") }
    }
}

@Composable
private fun ThresholdField(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = modifier
    )
}

@Composable
private fun DetailRow(
    label: String,
    value: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
