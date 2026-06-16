package pe.edu.upc.vacapp.campaign.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import pe.edu.upc.vacapp.campaign.domain.model.Campaign
import pe.edu.upc.vacapp.campaign.presentation.viewmodel.CampaignViewModel
import pe.edu.upc.vacapp.iam.presentation.view.components.AuthTextField
import pe.edu.upc.vacapp.iam.presentation.view.components.PrimaryButton

@Composable
fun FormCampaignView(
    goHome: () -> Unit,
    viewModel: CampaignViewModel
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf(LocalDate.now()) }
    var endDate by remember { mutableStateOf(LocalDate.now()) }
    val isLoading by viewModel.isLoading.collectAsState()
    val addSuccess by viewModel.addSuccess.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.resetAddSuccess()
    }

    LaunchedEffect(addSuccess) {
        if (addSuccess) {
            goHome()
            viewModel.resetAddSuccess()
        }
    }

    val submit = {
        if (!isLoading && name.isNotBlank()) {
            viewModel.addCanpaing(
                Campaign(
                    name = name,
                    description = description,
                    startDate = startDate,
                    endDate = endDate
                )
            )
        }
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
                    modifier = Modifier.padding(horizontal = 22.dp, vertical = 26.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    Text(
                        text = "Añadir campaña",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    AuthTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = "Nombre",
                        imeAction = ImeAction.Next
                    )

                    AuthTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = "Descripción",
                        imeAction = ImeAction.Next,
                        keyboardType = KeyboardType.Text
                    )

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )

                    CampaignDateField(
                        label = "Fecha de inicio",
                        date = startDate,
                        onDateChange = { startDate = it }
                    )

                    CampaignDateField(
                        label = "Fecha de fin",
                        date = endDate,
                        onDateChange = { endDate = it }
                    )

                    PrimaryButton(
                        label = "Guardar campaña",
                        onClick = { submit() },
                        isLoading = isLoading,
                        enabled = name.isNotBlank()
                    )

                    Spacer(modifier = Modifier.height(2.dp))

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
    }
}

@Composable
private fun CampaignDateField(
    label: String,
    date: LocalDate,
    onDateChange: (LocalDate) -> Unit
) {
    val context = LocalContext.current
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val dialog = remember {
        android.app.DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                onDateChange(LocalDate.of(year, month + 1, dayOfMonth))
            },
            date.year,
            date.monthValue - 1,
            date.dayOfMonth
        )
    }

    Surface(
        onClick = { dialog.show() },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        border = androidx.compose.foundation.BorderStroke(
            1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
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
                    text = date.format(formatter),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
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
