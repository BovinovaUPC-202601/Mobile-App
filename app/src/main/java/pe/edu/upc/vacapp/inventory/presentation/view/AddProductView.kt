package pe.edu.upc.vacapp.inventory.presentation.view

import android.app.DatePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import pe.edu.upc.vacapp.iam.presentation.view.components.AuthTextField
import pe.edu.upc.vacapp.iam.presentation.view.components.PrimaryButton
import pe.edu.upc.vacapp.inventory.domain.model.Product
import pe.edu.upc.vacapp.inventory.domain.model.ProductUnit
import pe.edu.upc.vacapp.inventory.presentation.viewmodel.InventoryViewModel
import pe.edu.upc.vacapp.shared.util.DateUtils
import pe.edu.upc.vacapp.ui.theme.Emerald30
import pe.edu.upc.vacapp.ui.theme.Error40
import java.util.Calendar

@Composable
fun AddProductView(
    goHome: () -> Unit,
    viewModel: InventoryViewModel,
    editProduct: Product? = null
) {
    val isEditing = editProduct != null

    var name by remember { mutableStateOf(editProduct?.name ?: "") }
    var quantity by remember { mutableStateOf(editProduct?.quantity?.toString() ?: "") }
    var selectedCategoryId by remember { mutableStateOf(editProduct?.categoryId ?: 0) }
    var selectedCategoryName by remember { mutableStateOf(editProduct?.categoryName ?: "") }
    var selectedUnit by remember { mutableStateOf(editProduct?.unit ?: "") }
    var expirationDate by remember { mutableStateOf(editProduct?.expirationDate ?: "") }
    var localError by remember { mutableStateOf("") }

    val isLoading by viewModel.isLoading.collectAsState()
    val addSuccess by viewModel.addSuccess.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.resetAddSuccess()
        viewModel.getCategories()
    }

    LaunchedEffect(addSuccess) {
        if (addSuccess) {
            goHome()
            viewModel.resetAddSuccess()
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(
                message = it,
                duration = SnackbarDuration.Short
            )
            viewModel.resetErrorMessage()
        }
    }

    val submit = {
        localError = ""
        if (!isLoading && name.isNotBlank()) {
            val qty = quantity.toIntOrNull() ?: 0
            if (qty <= 0) {
                localError = "La cantidad debe ser mayor a cero."
            } else if (selectedCategoryId == 0) {
                localError = "Selecciona una categoría."
            } else if (expirationDate.isNotBlank()) {
                try {
                    val expDate = LocalDate.parse(expirationDate)
                    if (expDate.isBefore(DateUtils.today())) {
                        localError = "La fecha de vencimiento debe ser hoy o una fecha futura."
                    } else {
                        val product = Product(
                            id = editProduct?.id ?: 0,
                            name = name,
                            categoryId = selectedCategoryId,
                            categoryName = selectedCategoryName,
                            quantity = qty,
                            unit = selectedUnit.ifBlank { null },
                            expirationDate = expirationDate.ifBlank { null }
                        )
                        if (isEditing) viewModel.updateProduct(product)
                        else viewModel.addProduct(product)
                    }
                } catch (_: Exception) {
                    localError = "Fecha de vencimiento inválida."
                }
            } else {
                val product = Product(
                    id = editProduct?.id ?: 0,
                    name = name,
                    categoryId = selectedCategoryId,
                    categoryName = selectedCategoryName,
                    quantity = qty,
                    unit = selectedUnit.ifBlank { null },
                    expirationDate = null
                )
                if (isEditing) viewModel.updateProduct(product)
                else viewModel.addProduct(product)
            }
        } else if (name.isBlank()) {
            localError = "El nombre del producto es obligatorio."
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                StyledSnackbar(data)
            }
        }
    ) { _ ->
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isEditing) "Editar producto" else "Nuevo producto",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                        )

                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        )

                        AuthTextField(
                            value = name,
                            onValueChange = { name = it; localError = "" },
                            label = "Nombre del producto",
                            imeAction = ImeAction.Next
                        )

                        if (categories.isNotEmpty()) {
                            CategoryDropdown(
                                categories = categories,
                                selectedCategoryName = selectedCategoryName,
                                onCategorySelected = { id, name ->
                                    selectedCategoryId = id
                                    selectedCategoryName = name
                                    localError = ""
                                }
                            )
                        }

                        AuthTextField(
                            value = quantity,
                            onValueChange = { newVal ->
                                if (newVal.all { it.isDigit() }) {
                                    quantity = newVal
                                    localError = ""
                                }
                            },
                            label = "Cantidad",
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        )

                        UnitDropdown(
                            selectedUnit = selectedUnit,
                            onUnitSelected = { selectedUnit = it; localError = "" }
                        )

                        DateField(
                            label = "Fecha de vencimiento (opcional)",
                            date = expirationDate,
                            onDateChange = { expirationDate = it; localError = "" }
                        )

                        if (localError.isNotBlank()) {
                            Text(
                                text = localError,
                                color = Error40,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        PrimaryButton(
                            label = if (isEditing) "Guardar cambios" else "Registrar producto",
                            onClick = submit,
                            isLoading = isLoading,
                            modifier = Modifier.height(52.dp)
                        )

                        TextButton(
                            onClick = goHome,
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Text(
                                text = "Cancelar",
                                color = Emerald30,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DateField(
    label: String,
    date: String,
    onDateChange: (String) -> Unit
) {
    val context = LocalContext.current

    val displayText = if (date.isNotBlank()) {
        try {
            val parsed = LocalDate.parse(date)
            parsed.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        } catch (_: Exception) {
            date
        }
    } else ""

    val calendar = Calendar.getInstance()
    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selected = LocalDate.of(year, month + 1, dayOfMonth)
                onDateChange(selected.toString())
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    Surface(
        onClick = { datePickerDialog.show() },
        modifier = Modifier
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
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = displayText.ifBlank { "Seleccionar fecha" },
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (displayText.isNotBlank()) MaterialTheme.colorScheme.onSurface
                        else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
                Icon(
                    imageVector = Icons.Filled.CalendarToday,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun CategoryDropdown(
    categories: List<pe.edu.upc.vacapp.inventory.domain.model.Category>,
    selectedCategoryName: String,
    onCategorySelected: (Int, String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val label = if (selectedCategoryName.isNotBlank()) selectedCategoryName else "Seleccionar categoría"

    Surface(
        onClick = { expanded = true },
        modifier = Modifier
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
                        text = "Categoría",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (selectedCategoryName.isNotBlank()) MaterialTheme.colorScheme.onSurface
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
                categories.forEach { category ->
                    DropdownMenuItem(
                        onClick = {
                            onCategorySelected(category.id, category.name)
                            expanded = false
                        },
                        text = {
                            Text(
                                text = category.name,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun UnitDropdown(
    selectedUnit: String,
    onUnitSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val displayLabel = if (selectedUnit.isNotBlank()) ProductUnit.displayValue(selectedUnit) else "Sin unidad"

    Surface(
        onClick = { expanded = true },
        modifier = Modifier
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
                        text = "Unidad",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = displayLabel,
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (selectedUnit.isNotBlank()) MaterialTheme.colorScheme.onSurface
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
                DropdownMenuItem(
                    onClick = {
                        onUnitSelected("")
                        expanded = false
                    },
                    text = {
                        Text(
                            text = "Sin unidad",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )
                ProductUnit.ALL.forEach { (value, label) ->
                    DropdownMenuItem(
                        onClick = {
                            onUnitSelected(value)
                            expanded = false
                        },
                        text = {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun StyledSnackbar(data: SnackbarData) {
    Snackbar(
        snackbarData = data,
        containerColor = MaterialTheme.colorScheme.inverseSurface,
        contentColor = MaterialTheme.colorScheme.inverseOnSurface,
        shape = RoundedCornerShape(12.dp)
    )
}
