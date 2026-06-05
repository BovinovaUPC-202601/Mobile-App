package pe.edu.upc.vacapp.animal.presentation.view

import android.app.DatePickerDialog
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import pe.edu.upc.vacapp.R
import pe.edu.upc.vacapp.animal.domain.model.Animal
import pe.edu.upc.vacapp.animal.domain.model.AnimalImage
import pe.edu.upc.vacapp.animal.presentation.viewmodel.AnimalViewModel
import pe.edu.upc.vacapp.barn.domain.model.Barn
import pe.edu.upc.vacapp.ui.theme.Color
import java.io.File
import java.util.Calendar
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

//@Preview(showBackground = true)
@Composable
fun AddAnimalForm(
    viewmodel: AnimalViewModel,
    goHome: () -> Unit,
    goAnimals: () -> Unit
) {


    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Add animal",
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 40.dp),
            color = Color.Black
        )

        AddAnimalCard(
            viewmodel,
            goHome = { goHome() },
            goAnimals = { goAnimals() }
        )
    }
}

// Constantes de validación biológica
private const val MIN_TEMP_LIMIT = 30.0
private const val MAX_TEMP_LIMIT = 45.0
private const val MIN_HR_LIMIT = 10
private const val MAX_HR_LIMIT = 150

//@Preview
@Composable
fun AddAnimalCard(
    viewmodel: AnimalViewModel,
    goHome: () -> Unit,
    goAnimals: () -> Unit
) {
    val colors = TextFieldDefaults.colors(
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        disabledContainerColor = Color.Transparent,
        errorContainerColor = Color.Transparent,
        focusedIndicatorColor = Color.Black,
        unfocusedIndicatorColor = Color.Black,
        disabledIndicatorColor = Color.Black,
        focusedLabelColor = Color.Black,
        unfocusedLabelColor = Color.Black,
        disabledLabelColor = Color.Black,
        disabledTextColor = Color.Black
    )

    val errorMessage = viewmodel.errorMessage.collectAsState().value
    val context = LocalContext.current
    val imageUri = remember { mutableStateOf<Uri?>(null) }
    val imageFile = remember { mutableStateOf<File?>(null) }
    val newAnimal = remember { mutableStateOf(Animal()) }
    val barns = viewmodel.barn.collectAsState()
    val addSuccess = viewmodel.addAnimalSuccess.collectAsState().value

    // Estado local para los errores de validación del formulario
    val localValidationError = remember { mutableStateOf("") }

    // Estado para el scroll
    val scrollState = rememberScrollState()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri.value = uri

        uri?.let {
            val inputStream = context.contentResolver.openInputStream(it)
            val tempFile = File.createTempFile("animal", ".jpg", context.cacheDir)
            inputStream?.use { input -> tempFile.outputStream().use { input.copyTo(it) } }
            imageFile.value = tempFile
            newAnimal.value = newAnimal.value.copy(image = AnimalImage.FromFile(tempFile))
        }
    }

    val icon = if (newAnimal.value.isMale) R.drawable.gender_male else R.drawable.gender_female

    LaunchedEffect(addSuccess) {
        if (addSuccess) {
            viewmodel.clearErrorMessage()
            viewmodel.clearAddAnimalSuccess()
            goAnimals()
        }
    }

    Card(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 10.dp) // Añadido un poco de padding vertical externo
            .fillMaxWidth() // Asegura que tome el ancho disponible
    ) {
        Column(
            modifier = Modifier
                .background(Color.AlmondCream)
                .verticalScroll(scrollState) // <-- AQUÍ ESTÁ LA MAGIA DEL SCROLL
                .padding(vertical = 16.dp), // Padding interno superior e inferior
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                fallback = painterResource(R.drawable.add_image_placeholder),
                model = imageUri.value,
                contentDescription = null,
                modifier = Modifier
                    .padding(vertical = 16.dp) // Reducido de 30 a 16
                    .size(240.dp, 160.dp) // Reducido de 300x200 para ahorrar espacio
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        launcher.launch("image/*")
                    },
                contentScale = ContentScale.Crop
            )

            TextField(
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .widthIn(min = 155.dp),
                colors = colors,
                trailingIcon = {
                    IconButton(
                        onClick = {
                            newAnimal.value = newAnimal.value.copy(isMale = !newAnimal.value.isMale)
                        }
                    ) {
                        Icon(
                            painterResource(icon),
                            null,
                            tint = Color.Black
                        )
                    }
                },
                label = { Text("Name") },
                value = newAnimal.value.name,
                onValueChange = { newAnimal.value = newAnimal.value.copy(name = it) },
                textStyle = TextStyle(color = Color.Black)
            )

            Column(
                modifier = Modifier.padding(horizontal = 30.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(
                        20.dp,
                        Alignment.CenterHorizontally
                    )
                ) {
                    TextField(
                        modifier = Modifier.weight(1f),
                        colors = colors,
                        value = newAnimal.value.breed,
                        label = { Text("Breed") },
                        onValueChange = { newAnimal.value = newAnimal.value.copy(breed = it) },
                        textStyle = TextStyle(color = Color.Black)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(
                        20.dp,
                        Alignment.CenterHorizontally
                    )
                ) {
                    DatePickerTextField(
                        label = "Birthdate",
                        date = newAnimal.value.birthDate,
                        onDateChange = {
                            newAnimal.value = newAnimal.value.copy(birthDate = it)
                        },
                        textStyle = TextStyle(color = Color.Black)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(
                        20.dp,
                        Alignment.CenterHorizontally
                    )
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        DropdownSelector(
                            label = "Barn",
                            items = barns.value,
                            onItemSelected = { barn ->
                                newAnimal.value = newAnimal.value.copy(barnId = barn.id)
                            },
                            textStyle = TextStyle(color = Color.Black)
                        )
                    }
                }

                // --- Fila de Umbrales de Temperatura ---
                Row(
                    horizontalArrangement = Arrangement.spacedBy(
                        10.dp, // Reducido el espacio entre inputs numéricos
                        Alignment.CenterHorizontally
                    ),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    NumberTextField(
                        label = "Temp. Mín (°C)",
                        initialValue = newAnimal.value.minTemperature,
                        onValueChange = { newAnimal.value = newAnimal.value.copy(minTemperature = it.toDouble()) },
                        modifier = Modifier.weight(1f)
                    )
                    NumberTextField(
                        label = "Temp. Máx (°C)",
                        initialValue = newAnimal.value.maxTemperature,
                        onValueChange = { newAnimal.value = newAnimal.value.copy(maxTemperature = it.toDouble()) },
                        modifier = Modifier.weight(1f)
                    )
                }

                // --- Fila de Umbrales de Ritmo Cardíaco ---
                Row(
                    horizontalArrangement = Arrangement.spacedBy(
                        10.dp, // Reducido el espacio entre inputs numéricos
                        Alignment.CenterHorizontally
                    ),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    NumberTextField(
                        label = "HR Mín (BPM)",
                        initialValue = newAnimal.value.minHeartRate,
                        onValueChange = { newAnimal.value = newAnimal.value.copy(minHeartRate = it.toInt()) },
                        modifier = Modifier.weight(1f)
                    )
                    NumberTextField(
                        label = "HR Máx (BPM)",
                        initialValue = newAnimal.value.maxHeartRate,
                        onValueChange = { newAnimal.value = newAnimal.value.copy(maxHeartRate = it.toInt()) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Errores de validación local (umbrales)
            if (localValidationError.value.isNotEmpty()) {
                Text(
                    text = localValidationError.value,
                    color = androidx.compose.ui.graphics.Color.Red,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                        .fillMaxWidth()
                )
            }

            // Errores que vienen del ViewModel
            if (!errorMessage.isNullOrEmpty()) {
                Text(
                    text = errorMessage,
                    color = androidx.compose.ui.graphics.Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                        .fillMaxWidth()
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 20.dp, top = 10.dp), // Ajustado el padding para los botones
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = {
                        viewmodel.clearErrorMessage()
                        localValidationError.value = ""
                        goHome()
                    }
                ) {
                    Icon(
                        painterResource(R.drawable.x_circle),
                        null,
                        modifier = Modifier.size(45.dp),
                        tint = Color.Black
                    )
                }

                // Botón de Guardar con Lógica de Validación
                IconButton(
                    onClick = {
                        val animal = newAnimal.value
                        localValidationError.value = ""

                        if (animal.minTemperature > animal.maxTemperature) {
                            localValidationError.value = "La temp. mínima no puede ser mayor a la máxima."
                        } else if (animal.minHeartRate > animal.maxHeartRate) {
                            localValidationError.value = "El HR mínimo no puede ser mayor al máximo."
                        } else if (animal.minTemperature < MIN_TEMP_LIMIT || animal.maxTemperature > MAX_TEMP_LIMIT) {
                            localValidationError.value = "La temp. debe estar entre $MIN_TEMP_LIMIT y $MAX_TEMP_LIMIT °C."
                        } else if (animal.minHeartRate < MIN_HR_LIMIT || animal.maxHeartRate > MAX_HR_LIMIT) {
                            localValidationError.value = "El ritmo cardíaco debe estar entre $MIN_HR_LIMIT y $MAX_HR_LIMIT BPM."
                        } else {
                            viewmodel.addAnimal(animal)
                        }
                    }
                ) {
                    Icon(
                        painterResource(R.drawable.check_circle), null,
                        modifier = Modifier.size(45.dp),
                        tint = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
fun NumberTextField(
    label: String,
    initialValue: Number?,
    onValueChange: (Number) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = TextFieldDefaults.colors(
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        disabledContainerColor = Color.Transparent,
        errorContainerColor = Color.Transparent,
        focusedIndicatorColor = Color.Black,
        unfocusedIndicatorColor = Color.Black,
        disabledIndicatorColor = Color.Black,
        focusedLabelColor = Color.Black,
        unfocusedLabelColor = Color.Black,
        disabledLabelColor = Color.Black,
        disabledTextColor = Color.Black
    )
    val textState = remember { mutableStateOf(initialValue?.toString() ?: "") }

    TextField(
        value = textState.value,
        onValueChange = { newText ->
            textState.value = newText

            // Parse y callback
            val parsedValue = when (initialValue) {
                is Int -> newText.toIntOrNull()
                is Double -> newText.toDoubleOrNull()
                else -> null
            }
            parsedValue?.let { onValueChange(it) }
        },
        label = { Text(label) },
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        modifier = modifier,
        colors = colors,
        textStyle = TextStyle(color = Color.Black)
    )
}


@Composable
fun DatePickerTextField(
    label: String,
    date: String,
    onDateChange: (String) -> Unit,
    textStyle: TextStyle = TextStyle.Default
) {
    val context = LocalContext.current
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val calendar = Calendar.getInstance()
    val colors = TextFieldDefaults.colors(
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        disabledContainerColor = Color.Transparent,
        errorContainerColor = Color.Transparent,
        focusedIndicatorColor = Color.Black,
        unfocusedIndicatorColor = Color.Black,
        disabledIndicatorColor = Color.Black,
        focusedLabelColor = Color.Black,
        unfocusedLabelColor = Color.Black,
        disabledLabelColor = Color.Black,
        disabledTextColor = Color.Black
    )
    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
                onDateChange(selectedDate.format(formatter))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        value = date,
        onValueChange = { },
        readOnly = true,
        label = { Text(label) },
        trailingIcon = {
            IconButton(onClick = { datePickerDialog.show() }) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = "Pick Date",
                    tint = Color.Black,
                )
            }
        },
        colors = colors,
        textStyle = textStyle
    )
}


@Composable
fun DropdownSelector(
    label: String,
    items: List<Barn>,
    onItemSelected: (Barn) -> Unit,
    textStyle: TextStyle = TextStyle.Default
) {
    val expanded = remember { mutableStateOf(false) }
    val selectedItem = remember { mutableStateOf<Barn?>(null) }
    val colors = TextFieldDefaults.colors(
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        disabledContainerColor = Color.Transparent,
        errorContainerColor = Color.Transparent,
        focusedIndicatorColor = Color.Black,
        unfocusedIndicatorColor = Color.Black,
        disabledIndicatorColor = Color.Black,
        focusedLabelColor = Color.Black,
        unfocusedLabelColor = Color.Black,
        disabledLabelColor = Color.Black,
        disabledTextColor = Color.Black
    )
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        TextField(
            value = selectedItem.value?.name ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown",
                    modifier = Modifier.clickable { expanded.value = true },
                    tint = Color.Black
                )
            },
            modifier = Modifier
                .fillMaxWidth(),
            colors = colors,
            textStyle = textStyle
        )

        DropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false }
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    onClick = {
                        selectedItem.value = item
                        expanded.value = false
                        onItemSelected(selectedItem.value!!)
                    },
                    text = { Text(text = item.name) }
                )
            }
        }
    }
}
