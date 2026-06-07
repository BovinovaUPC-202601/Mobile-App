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
import pe.edu.upc.vacapp.animal.presentation.viewmodel.AnimalViewModel
import pe.edu.upc.vacapp.barn.domain.model.Barn
import pe.edu.upc.vacapp.iam.presentation.view.components.AuthTextField
import pe.edu.upc.vacapp.iam.presentation.view.components.PrimaryButton
import pe.edu.upc.vacapp.ui.theme.Emerald40
import pe.edu.upc.vacapp.ui.theme.Emerald90
import java.io.File
import java.util.Calendar

@Composable
fun AddAnimalForm(
    viewmodel: AnimalViewModel,
    goHome: () -> Unit,
    goAnimals: () -> Unit
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
                goAnimals = goAnimals
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
    goAnimals: () -> Unit
) {
    val context = LocalContext.current
    val isLoading by viewmodel.isLoading.collectAsState()
    val addSuccess by viewmodel.addAnimalSuccess.collectAsState()
    val errorMessage by viewmodel.errorMessage.collectAsState()
    val barns by viewmodel.barn.collectAsState()

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
                text = "Add animal",
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
                            text = "Tap to add photo",
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
                    label = "Camera",
                    onClick = { launchCamera() },
                    modifier = Modifier.weight(1f)
                )
                ImageActionButton(
                    icon = Icons.Filled.PhotoLibrary,
                    label = "Gallery",
                    onClick = { galleryLauncher.launch("image/*") },
                    modifier = Modifier.weight(1f)
                )
            }

            // Name
            AuthTextField(
                value = animal.name,
                onValueChange = { animal = animal.copy(name = it) },
                label = "Name",
                imeAction = ImeAction.Next
            )

            // Breed + Gender toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AuthTextField(
                    value = animal.breed,
                    onValueChange = { animal = animal.copy(breed = it) },
                    label = "Breed",
                    imeAction = ImeAction.Next,
                    modifier = Modifier.weight(1f)
                )
                GenderToggle(
                    isMale = animal.isMale,
                    onClick = { animal = animal.copy(isMale = !animal.isMale) }
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
                selectedBarnId = animal.barnId,
                onBarnSelected = { animal = animal.copy(barnId = it) }
            )

            // Thresholds
            Text(
                text = "Thresholds",
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
                    label = "Temp. Min (°C)",
                    keyboardType = KeyboardType.Decimal,
                    modifier = Modifier.weight(1f)
                )
                AuthTextField(
                    value = formatDouble(animal.maxTemperature),
                    onValueChange = { animal = animal.copy(maxTemperature = it.toDoubleOrNull() ?: 0.0) },
                    label = "Temp. Max (°C)",
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
                    label = "HR Min (BPM)",
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.weight(1f)
                )
                AuthTextField(
                    value = formatInt(animal.maxHeartRate),
                    onValueChange = { animal = animal.copy(maxHeartRate = it.toIntOrNull() ?: 0) },
                    label = "HR Max (BPM)",
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
                label = "Save animal",
                onClick = {
                    localError = ""
                    val a = animal
                    when {
                        a.minTemperature > a.maxTemperature -> {
                            localError = "Min temp cannot exceed max temp."
                        }
                        a.minHeartRate > a.maxHeartRate -> {
                            localError = "Min heart rate cannot exceed max heart rate."
                        }
                        a.minTemperature < MIN_TEMP_LIMIT || a.maxTemperature > MAX_TEMP_LIMIT -> {
                            localError = "Temperature must be between $MIN_TEMP_LIMIT and $MAX_TEMP_LIMIT °C."
                        }
                        a.minHeartRate < MIN_HR_LIMIT || a.maxHeartRate > MAX_HR_LIMIT -> {
                            localError = "Heart rate must be between $MIN_HR_LIMIT and $MAX_HR_LIMIT BPM."
                        }
                        else -> viewmodel.addAnimal(a)
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
                    text = "Cancel",
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
    val calendar = Calendar.getInstance()
    val datePickerDialog = remember {
        android.app.DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selected = LocalDate.of(year, month + 1, dayOfMonth)
                onDateChange(selected.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
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
                    text = "Birthdate",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = date.ifBlank { "Select date" },
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
                        text = "Barn",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = selectedBarn?.name ?: "Select barn",
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
                    DropdownMenuItem(
                        onClick = {
                            onBarnSelected(barn.id)
                            expanded = false
                        },
                        text = { Text(barn.name) }
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
