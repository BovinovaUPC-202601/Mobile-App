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
        modifier = Modifier.padding(horizontal = 20.dp)
    ) {
        Column(
            modifier = Modifier.background(Color.AlmondCream),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                fallback = painterResource(R.drawable.add_image_placeholder),
                model = imageUri.value,
                contentDescription = null,
                modifier = Modifier
                    .padding(vertical = 30.dp)
                    .size(300.dp, 200.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        launcher.launch("image/*")
                    },
                contentScale = ContentScale.Crop
            )

            TextField(
                modifier = Modifier
                    .padding(bottom = 20.dp)
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
            }

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
                    .padding(end = 10.dp),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = {
                        viewmodel.clearErrorMessage()
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
                IconButton(
                    onClick = { viewmodel.addAnimal(newAnimal.value) }
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
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent
        )
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
