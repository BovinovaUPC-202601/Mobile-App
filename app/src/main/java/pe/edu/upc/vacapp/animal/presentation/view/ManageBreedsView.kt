package pe.edu.upc.vacapp.animal.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import pe.edu.upc.vacapp.animal.data.model.BreedRequest
import pe.edu.upc.vacapp.animal.domain.model.Breed
import pe.edu.upc.vacapp.animal.presentation.viewmodel.AnimalViewModel
import pe.edu.upc.vacapp.ui.theme.Emerald40
import pe.edu.upc.vacapp.ui.theme.Error40

@Composable
fun ManageBreedsView(
    viewModel: AnimalViewModel,
    onBack: () -> Unit
) {
    val breeds by viewModel.breeds.collectAsState()
    val breedError by viewModel.breedError.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val globalBreeds = breeds.filter { it.userId == null }
    val userBreeds = breeds.filter { it.userId != null }

    var editingBreed by remember { mutableStateOf<Breed?>(null) }
    var name by remember { mutableStateOf("") }
    var minTemperature by remember { mutableStateOf("30") }
    var maxTemperature by remember { mutableStateOf("45") }
    var minHeartRate by remember { mutableStateOf("10") }
    var maxHeartRate by remember { mutableStateOf("150") }
    var localError by remember { mutableStateOf("") }
    var deleteConfirm by remember { mutableStateOf<Breed?>(null) }

    LaunchedEffect(Unit) {
        viewModel.getBreeds()
        viewModel.clearBreedError()
    }

    val startCreate = {
        editingBreed = Breed(id = 0, name = "", minTemperature = 30.0, maxTemperature = 45.0, minHeartRate = 10, maxHeartRate = 150)
        name = ""
        minTemperature = "30"
        maxTemperature = "45"
        minHeartRate = "10"
        maxHeartRate = "150"
        localError = ""
        viewModel.clearBreedError()
    }

    val startEdit = { breed: Breed ->
        editingBreed = breed
        name = breed.name
        minTemperature = breed.minTemperature.toInt().toString()
        maxTemperature = breed.maxTemperature.toInt().toString()
        minHeartRate = breed.minHeartRate.toString()
        maxHeartRate = breed.maxHeartRate.toString()
        localError = ""
        viewModel.clearBreedError()
    }

    val cancelEdit = {
        editingBreed = null
        localError = ""
        viewModel.clearBreedError()
    }

    fun handleSave() {
        val trimmedName = name.trim()
        if (trimmedName.isEmpty()) {
            localError = "El nombre de la raza es obligatorio"
            return
        }
        val minTemp = minTemperature.toDoubleOrNull() ?: 0.0
        val maxTemp = maxTemperature.toDoubleOrNull() ?: 0.0
        val minHr = minHeartRate.toIntOrNull() ?: 0
        val maxHr = maxHeartRate.toIntOrNull() ?: 0
        if (minTemp > maxTemp) {
            localError = "La temperatura mínima no puede ser mayor a la máxima"
            return
        }
        if (minHr > maxHr) {
            localError = "El ritmo cardíaco mínimo no puede ser mayor al máximo"
            return
        }
        val request = BreedRequest(trimmedName, minTemp, maxTemp, minHr, maxHr)
        if (editingBreed?.id ?: 0 > 0) {
            viewModel.updateBreed(editingBreed!!.id, request)
        } else {
            viewModel.createBreed(request)
        }
        editingBreed = null
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (editingBreed != null) {
            BreedForm(
                name = name,
                onNameChange = { name = it; localError = "" },
                minTemperature = minTemperature,
                onMinTemperatureChange = { minTemperature = it },
                maxTemperature = maxTemperature,
                onMaxTemperatureChange = { maxTemperature = it },
                minHeartRate = minHeartRate,
                onMinHeartRateChange = { minHeartRate = it },
                maxHeartRate = maxHeartRate,
                onMaxHeartRateChange = { maxHeartRate = it },
                localError = localError,
                serverError = breedError,
                isLoading = isLoading,
                onSave = { handleSave() },
                onCancel = cancelEdit
            )
        } else {
            BreedsList(
                globalBreeds = globalBreeds,
                userBreeds = userBreeds,
                errorMessage = breedError,
                onEdit = { startEdit(it) },
                onDelete = { deleteConfirm = it },
                onAdd = startCreate
            )
        }
    }

    if (deleteConfirm != null) {
        AlertDialog(
            onDismissRequest = { deleteConfirm = null },
            title = { Text("Eliminar raza") },
            text = {
                Text("¿Estás seguro de que querés eliminar ${deleteConfirm!!.name}? Esta acción no se puede deshacer.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteBreed(deleteConfirm!!.id)
                        deleteConfirm = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Error40)
                ) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.onError)
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteConfirm = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun BreedsList(
    globalBreeds: List<Breed>,
    userBreeds: List<Breed>,
    errorMessage: String?,
    onEdit: (Breed) -> Unit,
    onDelete: (Breed) -> Unit,
    onAdd: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Global breeds
        Text(
            text = "Razas por defecto",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold
        )
        if (globalBreeds.isEmpty()) {
            Text(
                text = "No hay razas globales",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        } else {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                globalBreeds.forEach { breed ->
                    Text(
                        text = breed.name,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // User breeds
        Text(
            text = "Razas del rancho",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold
        )
        if (userBreeds.isEmpty()) {
            Text(
                text = "No tienes razas personalizadas",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        } else {
            userBreeds.forEach { breed ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = breed.name,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Temp: ${breed.minTemperature.toInt()}–${breed.maxTemperature.toInt()}°C | Ritmo: ${breed.minHeartRate}–${breed.maxHeartRate} BPM",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = { onEdit(breed) }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = { onDelete(breed) }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar",
                            tint = Error40
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onAdd,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Emerald40)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Añadir raza")
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun BreedForm(
    name: String,
    onNameChange: (String) -> Unit,
    minTemperature: String,
    onMinTemperatureChange: (String) -> Unit,
    maxTemperature: String,
    onMaxTemperatureChange: (String) -> Unit,
    minHeartRate: String,
    onMinHeartRateChange: (String) -> Unit,
    maxHeartRate: String,
    onMaxHeartRateChange: (String) -> Unit,
    localError: String,
    serverError: String?,
    isLoading: Boolean,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("Nombre") },
            placeholder = { Text("Angus") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Text(
            text = "Umbrales biométricos",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = minTemperature,
                onValueChange = onMinTemperatureChange,
                label = { Text("Temp. Mín (°C)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            )
            OutlinedTextField(
                value = maxTemperature,
                onValueChange = onMaxTemperatureChange,
                label = { Text("Temp. Máx (°C)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = minHeartRate,
                onValueChange = onMinHeartRateChange,
                label = { Text("Ritmo Mín (BPM)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            )
            OutlinedTextField(
                value = maxHeartRate,
                onValueChange = onMaxHeartRateChange,
                label = { Text("Ritmo Máx (BPM)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            )
        }

        if (localError.isNotEmpty()) {
            Text(
                text = localError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        if (serverError != null) {
            Text(
                text = serverError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading
            ) {
                Text("Cancelar")
            }
            Button(
                onClick = onSave,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Emerald40),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Guardar")
                }
            }
        }
    }
}
