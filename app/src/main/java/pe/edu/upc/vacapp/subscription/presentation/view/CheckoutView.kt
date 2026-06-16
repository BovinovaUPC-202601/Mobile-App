package pe.edu.upc.vacapp.subscription.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import pe.edu.upc.vacapp.iam.presentation.view.components.PrimaryButton
import pe.edu.upc.vacapp.subscription.domain.model.CheckoutSession
import pe.edu.upc.vacapp.subscription.presentation.viewmodel.CheckoutPhase
import pe.edu.upc.vacapp.ui.theme.Emerald40
import pe.edu.upc.vacapp.ui.theme.OnSurfaceVariantLight

// Pure simulation, mirroring the web MockCheckoutPage: per-field validation, Luhn
// check and expiry rejection. No real gateway.

private fun passesLuhn(digits: String): Boolean {
    var sum = 0
    var even = false
    for (i in digits.indices.reversed()) {
        var d = digits[i].digitToIntOrNull() ?: return false
        if (even) { d *= 2; if (d > 9) d -= 9 }
        sum += d
        even = !even
    }
    return sum % 10 == 0
}

private fun validateExpiry(value: String): String? {
    val m = Regex("""^(\d{2})/(\d{2})$""").matchEntire(value) ?: return "Formato MM/AA."
    val month = m.groupValues[1].toInt()
    val year = 2000 + m.groupValues[2].toInt()
    if (month < 1 || month > 12) return "Mes inválido."
    // Reject months strictly in the past (compares year*12+month).
    val now = java.util.Calendar.getInstance()
    val nowKey = now.get(java.util.Calendar.YEAR) * 12 + (now.get(java.util.Calendar.MONTH) + 1)
    if (year * 12 + month < nowKey) return "Tarjeta vencida."
    return null
}

@Composable
fun CheckoutView(
    session: CheckoutSession,
    phase: CheckoutPhase,
    formError: String?,
    onPay: () -> Unit,
    onCancel: () -> Unit
) {
    when (phase) {
        CheckoutPhase.Processing -> CheckoutStatus(
            icon = { CircularProgressIndicator(color = Emerald40) },
            title = "Procesando pago…",
            subtitle = "No cierres esta pantalla."
        )
        CheckoutPhase.Success -> CheckoutStatus(
            icon = { Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF22C55E), modifier = Modifier.size(72.dp)) },
            title = "¡Pago exitoso!",
            subtitle = "${session.label} activado por S/${session.amount}/mes."
        )
        CheckoutPhase.Form -> CheckoutForm(session, formError, onPay, onCancel)
    }
}

@Composable
private fun CheckoutStatus(icon: @Composable () -> Unit, title: String, subtitle: String) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        icon()
        Text(title, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
        Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariantLight)
    }
}

@Composable
private fun CheckoutForm(
    session: CheckoutSession,
    formError: String?,
    onPay: () -> Unit,
    onCancel: () -> Unit
) {
    var number by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var expiry by remember { mutableStateOf("") }
    var cvc by remember { mutableStateOf("") }
    var errNumber by remember { mutableStateOf<String?>(null) }
    var errName by remember { mutableStateOf<String?>(null) }
    var errExpiry by remember { mutableStateOf<String?>(null) }
    var errCvc by remember { mutableStateOf<String?>(null) }

    fun formatNumber(v: String) = v.filter { it.isDigit() }.take(16).chunked(4).joinToString(" ")
    fun formatExpiry(v: String): String {
        val d = v.filter { it.isDigit() }.take(4)
        return if (d.length > 2) "${d.take(2)}/${d.drop(2)}" else d
    }

    fun validate(): Boolean {
        val digits = number.filter { it.isDigit() }
        errNumber = when {
            digits.isEmpty() -> "Ingresá el número de tarjeta."
            digits.length != 16 -> "Debe tener 16 dígitos."
            !passesLuhn(digits) -> "Número de tarjeta inválido."
            else -> null
        }
        errName = when {
            name.isBlank() -> "Ingresá el nombre."
            !Regex("""^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$""").matches(name.trim()) -> "Solo letras."
            else -> null
        }
        errExpiry = validateExpiry(expiry)
        errCvc = if (!Regex("""^\d{3}$""").matches(cvc)) "CVC de 3 dígitos." else null
        return listOf(errNumber, errName, errExpiry, errCvc).all { it == null }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(Icons.Default.Lock, null, tint = OnSurfaceVariantLight, modifier = Modifier.size(16.dp))
            Text("Pago seguro (simulado)", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariantLight)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(session.label, style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariantLight)
            Text("S/${session.amount} / mes", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
        }

        OutlinedTextField(
            value = number,
            onValueChange = { number = formatNumber(it); errNumber = null },
            label = { Text("Número de tarjeta") },
            placeholder = { Text("4242 4242 4242 4242") },
            leadingIcon = { Icon(Icons.Default.CreditCard, null) },
            singleLine = true,
            isError = errNumber != null,
            supportingText = errNumber?.let { { Text(it) } },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = name,
            onValueChange = { name = it; errName = null },
            label = { Text("Nombre en la tarjeta") },
            placeholder = { Text("JUAN PEREZ") },
            singleLine = true,
            isError = errName != null,
            supportingText = errName?.let { { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = expiry,
                onValueChange = { expiry = formatExpiry(it); errExpiry = null },
                label = { Text("Vencimiento") },
                placeholder = { Text("MM/AA") },
                singleLine = true,
                isError = errExpiry != null,
                supportingText = errExpiry?.let { { Text(it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = cvc,
                onValueChange = { cvc = it.filter { c -> c.isDigit() }.take(3); errCvc = null },
                label = { Text("CVC") },
                placeholder = { Text("123") },
                singleLine = true,
                isError = errCvc != null,
                supportingText = errCvc?.let { { Text(it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.width(120.dp)
            )
        }

        Text(
            "Pago simulado: usá una tarjeta de prueba válida (ej. 4242 4242 4242 4242) y un vencimiento futuro. No se hace ningún cargo real.",
            style = MaterialTheme.typography.bodySmall,
            color = OnSurfaceVariantLight
        )

        formError?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }

        PrimaryButton(
            label = "Pagar S/${session.amount}",
            onClick = { if (validate()) onPay() },
            showTrailingIcon = false
        )
        OutlinedButton(onClick = onCancel, modifier = Modifier.fillMaxWidth()) { Text("Cancelar") }
    }
}
