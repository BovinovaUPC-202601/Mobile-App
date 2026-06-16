package pe.edu.upc.vacapp.barn.presentation.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import pe.edu.upc.vacapp.barn.domain.model.Barn
import pe.edu.upc.vacapp.barn.presentation.viewmodel.BarnViewModel
import pe.edu.upc.vacapp.iam.presentation.view.components.AuthTextField
import pe.edu.upc.vacapp.iam.presentation.view.components.PrimaryButton

@Composable
fun FormBarnView(
    viewModel: BarnViewModel,
    goHome: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var limit by remember { mutableStateOf("") }
    val isLoading by viewModel.isLoading.collectAsState()
    val saveSuccess by viewModel.saveSuccess.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.resetSaveSuccess()
    }

    LaunchedEffect(saveSuccess) {
        if (saveSuccess == true) {
            goHome()
            viewModel.resetSaveSuccess()
        }
    }

    val submit = {
        if (!isLoading) {
            viewModel.addBarn(Barn(name = name, limit = limit))
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
                text = "Añadir establo",
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
                value = limit,
                onValueChange = { input -> if (input.all(Char::isDigit)) limit = input },
                label = "Capacidad (bovinos)",
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done,
                onImeAction = { submit() }
            )

            PrimaryButton(
                label = "Guardar establo",
                onClick = { submit() },
                isLoading = isLoading,
                enabled = name.isNotBlank() && limit.isNotBlank()
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
