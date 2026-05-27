package pe.edu.upc.vacapp.alerts.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.edu.upc.vacapp.alerts.domain.model.Alert
import pe.edu.upc.vacapp.alerts.presentation.viewmodel.AlertViewModel
import pe.edu.upc.vacapp.ui.theme.Color as AppColor

@Composable
fun AlertView(viewModel: AlertViewModel, userId: Int) {
    val alerts  by viewModel.alerts.collectAsState()
    val loading by viewModel.loading.collectAsState()

    LaunchedEffect(userId) {
        viewModel.loadAlerts(userId)
    }

    val unreadCount = alerts.count { it.isUnread }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColor.LightGray)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Alertas Sanitarias", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = AppColor.Green)
            if (unreadCount > 0) {
                Badge(containerColor = Color.Red) {
                    Text("$unreadCount", color = Color.White, fontSize = 11.sp)
                }
            }
        }

        if (loading) {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AppColor.Green)
            }
        }

        if (!loading && alerts.isEmpty()) {
            Text("Sin alertas registradas.", color = Color.Gray, fontSize = 14.sp)
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(alerts) { alert ->
                AlertItemCard(alert = alert, onMarkAsRead = { viewModel.markAsRead(alert.id) })
            }
        }
    }
}

@Composable
fun AlertItemCard(alert: Alert, onMarkAsRead: () -> Unit) {
    val bgColor = when {
        alert.isRed    -> Color(0xFFFFEBEE)
        alert.isYellow -> Color(0xFFFFFDE7)
        else           -> Color(0xFFE8F5E9)
    }
    val borderColor = when {
        alert.isRed    -> Color(0xFFEF9A9A)
        alert.isYellow -> Color(0xFFFFE082)
        else           -> Color(0xFFA5D6A7)
    }
    val urgencyEmoji = when {
        alert.isRed    -> "🔴"
        alert.isYellow -> "🟡"
        else           -> "🟢"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(borderColor)
        )
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "$urgencyEmoji ${alert.message}",
                    fontSize = 13.sp,
                    fontWeight = if (alert.isUnread) FontWeight.SemiBold else FontWeight.Normal,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AssistChip(onClick = {}, label = { Text(alert.alertType, fontSize = 11.sp) })
                AssistChip(onClick = {}, label = { Text(alert.urgencyLevel, fontSize = 11.sp) })
                AssistChip(
                    onClick = {},
                    label = { Text(if (alert.isUnread) "No leída" else "Leída", fontSize = 11.sp) }
                )
            }

            Text("Bovino ID: ${alert.bovineId} · ${alert.createdAt.take(16).replace("T", " ")}",
                fontSize = 11.sp, color = Color.Gray)

            if (alert.isUnread) {
                Button(
                    onClick = onMarkAsRead,
                    colors = ButtonDefaults.buttonColors(containerColor = AppColor.Green),
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 6.dp)
                ) {
                    Text("Marcar como leída", color = Color.White, fontSize = 13.sp)
                }
            }
        }
    }
}
