package pe.edu.upc.vacapp.subscription.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pe.edu.upc.vacapp.iam.presentation.view.components.PrimaryButton
import pe.edu.upc.vacapp.subscription.domain.model.Plan
import pe.edu.upc.vacapp.subscription.presentation.viewmodel.SubscriptionViewModel
import pe.edu.upc.vacapp.ui.theme.Emerald30
import pe.edu.upc.vacapp.ui.theme.Emerald40
import pe.edu.upc.vacapp.ui.theme.Emerald90
import pe.edu.upc.vacapp.ui.theme.OnSurfaceVariantLight
import pe.edu.upc.vacapp.ui.theme.Stone90

@Composable
fun SubscriptionView(
    viewModel: SubscriptionViewModel
) {
    val plans by viewModel.plans.collectAsState()
    val current by viewModel.current.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val actionInProgress by viewModel.actionInProgress.collectAsState()
    val error by viewModel.error.collectAsState()

    // Effective current plan: a cancelled/suspended Plus counts as Free.
    val currentPlanName = current?.let { if (it.isPlusActive) "Plus" else "Free" } ?: "Free"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Tu suscripción",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
        )
        Text(
            text = "Elegí el plan que mejor se adapta a tu operación.",
            style = MaterialTheme.typography.bodyMedium,
            color = OnSurfaceVariantLight
        )

        if (current?.isPlusActive == true) {
            CurrentPlusSummary(
                monthlyCost = current!!.monthlyCost,
                nextRenewal = current!!.nextRenewal
            )
        }

        if (loading && plans.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Emerald40)
            }
        }

        error?.let { msg ->
            Text(
                text = msg,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        plans.forEach { plan ->
            PlanCard(
                plan = plan,
                isCurrent = plan.name.equals(currentPlanName, ignoreCase = true),
                actionInProgress = actionInProgress,
                onActivate = { viewModel.activatePlus() },
                onDowngrade = { viewModel.cancel() }
            )
        }
    }
}

@Composable
private fun CurrentPlusSummary(monthlyCost: Double, nextRenewal: String?) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Emerald90
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "Plan Plus activo",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = Emerald30
            )
            Text(
                text = "Costo mensual: S/${formatPrice(monthlyCost)}",
                style = MaterialTheme.typography.bodyMedium,
                color = Emerald30
            )
            nextRenewal?.takeIf { it.isNotBlank() }?.let {
                Text(
                    text = "Próxima renovación: ${it.take(10)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Emerald30
                )
            }
        }
    }
}

@Composable
private fun PlanCard(
    plan: Plan,
    isCurrent: Boolean,
    actionInProgress: Boolean,
    onActivate: () -> Unit,
    onDowngrade: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = if (plan.isPlus) 6.dp else 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (plan.isPlus) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Emerald40,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(
                    text = plan.name,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(start = if (plan.isPlus) 6.dp else 0.dp)
                )
                if (isCurrent) {
                    CurrentBadge(modifier = Modifier.padding(start = 8.dp))
                }
            }

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "S/${formatPrice(plan.monthlyPrice)}",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = " / mes",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVariantLight,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            plan.features.forEach { feature ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Emerald40,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = feature,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            if (plan.isPlus && plan.additionalCollarMonthly > 0) {
                Text(
                    text = "Collar adicional: S/${formatPrice(plan.additionalCollarMonthly)} / mes",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariantLight
                )
            }

            when {
                isCurrent -> OutlinedButton(
                    onClick = {},
                    enabled = false,
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Plan actual") }

                plan.isPlus -> PrimaryButton(
                    label = "Mejorar a Plus",
                    onClick = onActivate,
                    isLoading = actionInProgress,
                    showTrailingIcon = false
                )

                else -> OutlinedButton(
                    onClick = onDowngrade,
                    enabled = !actionInProgress,
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Cambiar a Free") }
            }
        }
    }
}

@Composable
private fun CurrentBadge(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(Stone90)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = "Actual",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color = Emerald30
        )
    }
}

/** Drops the trailing ".0" so prices read "149" / "25" but keeps real decimals if any. */
private fun formatPrice(value: Double): String =
    if (value % 1.0 == 0.0) value.toInt().toString() else value.toString()
