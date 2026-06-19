package pe.edu.upc.vacapp.animal.presentation.view

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil3.compose.AsyncImage
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import pe.edu.upc.vacapp.animal.domain.model.Animal
import pe.edu.upc.vacapp.animal.domain.model.AnimalImage
import pe.edu.upc.vacapp.animal.domain.model.Breed
import pe.edu.upc.vacapp.animal.presentation.viewmodel.AnimalViewModel
import pe.edu.upc.vacapp.barn.domain.model.Barn
import pe.edu.upc.vacapp.iam.presentation.view.components.AuthTextField
import pe.edu.upc.vacapp.iam.presentation.view.components.PrimaryButton
import pe.edu.upc.vacapp.shared.util.DateUtils
import pe.edu.upc.vacapp.ui.theme.Emerald40
import pe.edu.upc.vacapp.ui.theme.Emerald90
import java.io.File
import java.util.Calendar
import java.util.TimeZone

@Composable
fun AddAnimalForm(
    viewmodel: AnimalViewModel,
    goHome: () -> Unit,
    goAnimals: () -> Unit,
    onManageBreeds: () -> Unit = {}
) {
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            FormAnimalView(
                viewmodel = viewmodel,
                goHome = goHome,
                goAnimals = goAnimals,
                onManageBreeds = onManageBreeds
            )
        }
    }
}

private const val MIN_TEMP_LIMIT = 30.0
private const val MAX_TEMP_LIMIT = 45.0
private const val MIN_HR_LIMIT = 10
private const val MAX_HR_LIMIT = 150

@Composable
private fun FormAnimalView(
    viewmodel: AnimalViewModel,
    goHome: () -> Unit,
    goAnimals: () -> Unit,
    onManageBreeds: () -> Unit = {}
) {
    val context = LocalContext.current
    val isLoading by viewmodel.isLoading.collectAsState()
    val addSuccess by viewmodel.addAnimalSuccess.collectAsState()
    val errorMessage by viewmodel.errorMessage.collectAsState()
    val barns by viewmodel.barn.collectAsState()
    val breeds by viewmodel.breeds.collectAsState()
    val animals by viewmodel.animals.collectAsState()

    var animal by remember { mutableStateOf(Animal()) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imageFile by remember { mutableStateOf<File?>(null) }
    var localError by remember { mutableStateOf("") }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imageUri = it
            val inputStream = context.contentResolver.openInputStream(it)
            val tempFile = File.createTempFile("animal_", ".jpg", context.cacheDir)
            inputStream?.use { input -> tempFile.outputStream().use { input.copyTo(it) } }
            imageFile = tempFile
            animal = animal.copy(image = AnimalImage.FromFile(tempFile))
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            imageFile?.let { file ->
                val uri = FileProvider.getUriForFile(
                    context, context.packageName + ".fileprovider", file
                )
                imageUri = uri
                animal = animal.copy(image = AnimalImage.FromFile(file))
            }
        }
    }

    fun launchCamera() {
        val tempFile = File.createTempFile("camera_", ".jpg", context.cacheDir)
        imageFile = tempFile
        val uri = FileProvider.getUriForFile(
            context, context.packageName + ".fileprovider", tempFile
        )
        cameraLauncher.launch(uri)
    }

    LaunchedEffect(Unit) {
        viewmodel.clearAddAnimalSuccess()
    }

    LaunchedEffect(addSuccess) {
        if (addSuccess) {
            viewmodel.clearErrorMessage()
            viewmodel.clearAddAnimalSuccess()
            goAnimals()
        }
    }

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
            modifier = Modifier.padding(horizontal = 22.dp, vertical = 26.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Text(
                text = "Añadir animal",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Image picker area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Emerald90)
                    .clickable { galleryLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (imageUri != null) {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CameraAlt,
                            contentDescription = null,
                            tint = Emerald40,
                            modifier = Modifier.size(40.dp)
                        )
                        Text(
                            text = "Toca para añadir foto",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Emerald40
                        )
                    }
                }
            }

            // Quick-action buttons: Camera / Gallery
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ImageActionButton(
                    icon = Icons.Filled.CameraAlt,
                    label = "Cámara",
                    onClick = { launchCamera() },
                    modifier = Modifier.weight(1f)
                )
                ImageActionButton(
                    icon = Icons.Filled.PhotoLibrary,
                    label = "Galería",
                    onClick = { galleryLauncher.launch("image/*") },
                    modifier = Modifier.weight(1f)
                )
            }

            // Name
            AuthTextField(
                value = animal.name,
                onValueChange = { animal = animal.copy(name = it) },
                label = "Nombre",
                imeAction = ImeAction.Next
            )

            // Breed dropdown + Gender toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BreedDropdown(
                    breeds = breeds,
                    selectedBreedName = animal.breed,
                    onBreedSelected = { breed ->
                        animal = animal.copy(
                            breed = breed.name,
                            minTemperature = breed.minTemperature,
                            maxTemperature = breed.maxTemperature,
                            minHeartRate = breed.minHeartRate,
                            maxHeartRate = breed.maxHeartRate
                        )
                    },
                    modifier = Modifier.weight(1f)
                )
                GenderToggle(
                    isMale = animal.isMale,
                    onClick = { animal = animal.copy(isMale = !animal.isMale) }
                )
            }

            TextButton(
                onClick = onManageBreeds,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(
                    text = "Administrar razas",
                    style = MaterialTheme.typography.labelMedium,
                    color = Emerald40
                )
            }

            // Birthdate
            DatePickerField(
                date = animal.birthDate,
                onDateChange = { animal = animal.copy(birthDate = it) }
            )

            // Barn dropdown
            BarnDropdown(
                barns = barns,
                animals = animals,
                selectedBarnId = animal.barnId,
                onBarnSelected = { animal = animal.copy(barnId = it) }
            )

            // Thresholds
            Text(
                text = "Umbrales",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AuthTextField(
                    value = formatDouble(animal.minTemperature),
                    onValueChange = { animal = animal.copy(minTemperature = it.toDoubleOrNull() ?: 0.0) },
                    label = "Temp. mín (°C)",
                    keyboardType = KeyboardType.Decimal,
                    modifier = Modifier.weight(1f)
                )
                AuthTextField(
                    value = formatDouble(animal.maxTemperature),
                    onValueChange = { animal = animal.copy(maxTemperature = it.toDoubleOrNull() ?: 0.0) },
                    label = "Temp. máx (°C)",
                    keyboardType = KeyboardType.Decimal,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AuthTextField(
                    value = formatInt(animal.minHeartRate),
                    onValueChange = { animal = animal.copy(minHeartRate = it.toIntOrNull() ?: 0) },
                    label = "Pulso mín (BPM)",
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.weight(1f)
                )
                AuthTextField(
                    value = formatInt(animal.maxHeartRate),
                    onValueChange = { animal = animal.copy(maxHeartRate = it.toIntOrNull() ?: 0) },
                    label = "Pulso máx (BPM)",
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.weight(1f)
                )
            }

            // Local validation error
            if (localError.isNotEmpty()) {
                Text(
                    text = localError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // ViewModel error
            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Save
            PrimaryButton(
                label = "Guardar animal",
                onClick = {
                    localError = ""
                    val a = animal
                    when {
                        a.barnId == 0 -> localError = "Debes seleccionar un establo."
                        a.breed.isBlank() -> localError = "Debes seleccionar una raza."
                        a.birthDate.isBlank() -> localError = "Debes seleccionar una fecha de nacimiento."
                        else -> {
                            val birthLocalDate = try {
                                LocalDate.parse(a.birthDate, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                            } catch (_: Exception) { null }
                            if (birthLocalDate == null || !birthLocalDate.isBefore(DateUtils.today())) {
                                localError = "La fecha de nacimiento debe ser anterior a hoy."
                            } else when {
                                a.minTemperature > a.maxTemperature -> {
                                    localError = "La temp. mín no puede superar la temp. máx."
                                }
                                a.minHeartRate > a.maxHeartRate -> {
                                    localError = "El pulso mín no puede superar el pulso máx."
                                }
                                a.minTemperature < MIN_TEMP_LIMIT || a.maxTemperature > MAX_TEMP_LIMIT -> {
                                    localError = "La temperatura debe estar entre $MIN_TEMP_LIMIT y $MAX_TEMP_LIMIT °C."
                                }
                                a.minHeartRate < MIN_HR_LIMIT || a.maxHeartRate > MAX_HR_LIMIT -> {
                                    localError = "El pulso debe estar entre $MIN_HR_LIMIT y $MAX_HR_LIMIT BPM."
                                }
                                else -> viewmodel.addAnimal(a)
                            }
                        }
                    }
                },
                isLoading = isLoading,
                enabled = animal.name.isNotBlank()
            )

            // Cancel
            TextButton(
                onClick = goHome,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                enabled = !isLoading
            ) {
                Text(
                    text = "Cancelar",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun GenderToggle(
    isMale: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isMale) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                else MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (isMale) "\u2642" else "\u2640",
            style = MaterialTheme.typography.titleLarge,
            color = if (isMale) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.error
        )
    }
}

@Composable
private fun ImageActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(40.dp),
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DatePickerField(
    date: String,
    onDateChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val peruCal = Calendar.getInstance(TimeZone.getTimeZone("America/Lima"))
    val datePickerDialog = remember {
        android.app.DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selected = LocalDate.of(year, month + 1, dayOfMonth)
                onDateChange(selected.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
            },
            peruCal.get(Calendar.YEAR),
            peruCal.get(Calendar.MONTH),
            peruCal.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.maxDate = peruCal.timeInMillis
        }
    }

    Surface(
        onClick = { datePickerDialog.show() },
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Fecha de nacimiento",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = date.ifBlank { "Seleccionar fecha" },
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (date.isBlank()) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    else MaterialTheme.colorScheme.onSurface
                )
            }
            Icon(
                imageVector = Icons.Filled.CalendarToday,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun BarnDropdown(
    barns: List<Barn>,
    animals: List<Animal>,
    selectedBarnId: Int,
    onBarnSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedBarn = barns.find { it.id == selectedBarnId }

    Surface(
        onClick = { expanded = true },
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
    ) {
        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Establo",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = selectedBarn?.name ?: "Seleccionar establo",
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (selectedBarn != null) MaterialTheme.colorScheme.onSurface
                        else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                barns.forEach { barn ->
                    val currentCount = animals.count { it.barnId == barn.id }
                    val limit = barn.limit.toIntOrNull() ?: 0
                    val label = if (limit > 0) "${barn.name} ($currentCount/$limit)" else barn.name
                    val isFull = limit > 0 && currentCount >= limit
                    DropdownMenuItem(
                        onClick = {
                            if (!isFull) {
                                onBarnSelected(barn.id)
                                expanded = false
                            }
                        },
                        text = {
                            Text(
                                text = label,
                                color = if (isFull) MaterialTheme.colorScheme.error
                                else MaterialTheme.colorScheme.onSurface
                            )
                        },
                        enabled = !isFull
                    )
                }
            }
        }
    }
}

@Composable
private fun BreedDropdown(
    breeds: List<Breed>,
    selectedBreedName: String,
    onBreedSelected: (Breed) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedBreed = breeds.find { it.name == selectedBreedName }

    Surface(
        onClick = { expanded = true },
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
    ) {
        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Raza",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = selectedBreed?.name ?: "Seleccionar raza",
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (selectedBreed != null) MaterialTheme.colorScheme.onSurface
                        else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                breeds.forEach { breed ->
                    DropdownMenuItem(
                        onClick = {
                            onBreedSelected(breed)
                            expanded = false
                        },
                        text = { Text(breed.name) }
                    )
                }
            }
        }
    }
}

private fun formatDouble(value: Double): String =
    if (value == 0.0) "" else value.toString()

private fun formatInt(value: Int): String =
    if (value == 0) "" else value.toString()
