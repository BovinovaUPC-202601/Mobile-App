package pe.edu.upc.vacapp.monitoring.presentation.view

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.edu.upc.vacapp.monitoring.domain.model.HealthRecord
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.math.max
import kotlin.math.min

private const val HOUR_MS = 60L * 60L * 1000L

private val TempColor = Color(0xFFF97316) // orange
private val BpmColor  = Color(0xFFEF4444) // red

/** Parses the backend timestamp (ISO, with or without offset) to epoch millis (UTC). */
private fun parseMillis(raw: String): Long = try {
    OffsetDateTime.parse(raw).toInstant().toEpochMilli()
} catch (_: Exception) {
    try {
        java.time.LocalDateTime.parse(raw, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            .toInstant(ZoneOffset.UTC).toEpochMilli()
    } catch (_: Exception) { 0L }
}

/**
 * Live telemetry chart (Compose Canvas), mirroring the web LiveTelemetryChart:
 * dual series (temperature orange / heart-rate red, independent scales), dashed
 * reference lines for the bovine's safe ranges, a time-window slider and an
 * "EN VIVO" indicator that flashes when a fresh reading lands.
 */
@Composable
fun LiveTelemetryChart(records: List<HealthRecord>) {
    var hoursBack by remember { mutableFloatStateOf(6f) }

    // Records arrive newest-first; sort ascending by time once.
    val sorted = remember(records) { records.sortedBy { parseMillis(it.recordedAt) } }

    val points = remember(sorted, hoursBack) {
        val from = System.currentTimeMillis() - (hoursBack.toLong() * HOUR_MS)
        sorted.filter { parseMillis(it.recordedAt) >= from }
    }

    // Flash the live dot when the newest reading id changes.
    var live by remember { mutableStateOf(false) }
    val lastId = remember { mutableStateOf<Int?>(null) }
    LaunchedEffect(records) {
        val newest = records.firstOrNull()?.id
        if (newest != null && lastId.value != null && newest != lastId.value) {
            live = true
        }
        lastId.value = newest
    }
    LaunchedEffect(live) {
        if (live) { kotlinx.coroutines.delay(900); live = false }
    }
    val liveAlpha by animateFloatAsState(if (live) 1f else 0.4f, label = "liveAlpha")

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Telemetría en vivo", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Box(
                Modifier.size(8.dp).clip(CircleShape)
                    .background(Color(0xFFEF4444).copy(alpha = liveAlpha))
            )
            Text("EN VIVO", fontSize = 11.sp, color = Color.Gray.copy(alpha = liveAlpha))
        }

        Text(
            "Últimas ${hoursBack.toInt()} h (${points.size} lecturas)",
            fontSize = 11.sp, color = Color.Gray
        )
        Slider(
            value = hoursBack,
            onValueChange = { hoursBack = it },
            valueRange = 1f..168f,
            steps = 0,
            colors = SliderDefaults.colors(thumbColor = TempColor, activeTrackColor = TempColor)
        )
        // Quick-pick labels mirroring the web marks.
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            listOf(6 to "6h", 24 to "1d", 72 to "3d", 168 to "1sem").forEach { (h, label) ->
                Text(
                    label, fontSize = 10.sp,
                    color = if (hoursBack.toInt() == h) TempColor else Color.Gray,
                    modifier = Modifier.clickable { hoursBack = h.toFloat() }
                )
            }
        }

        if (points.isEmpty()) {
            Text("Sin telemetría en este rango.", fontSize = 13.sp, color = Color.Gray)
        } else {
            ChartLegend()
            TelemetryCanvas(points)
        }
    }
}

@Composable
private fun ChartLegend() {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        LegendDot(TempColor, "Temp °C")
        LegendDot(BpmColor, "BPM")
    }
}

@Composable
private fun LegendDot(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Box(Modifier.size(10.dp).clip(CircleShape).background(color))
        Text(label, fontSize = 11.sp, color = Color.Gray)
    }
}

@Composable
private fun TelemetryCanvas(points: List<HealthRecord>) {
    val gridColor = MaterialTheme.colorScheme.outlineVariant
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .padding(vertical = 8.dp)
    ) {
        val padL = 8f; val padR = 8f; val padT = 12f; val padB = 12f
        val w = size.width - padL - padR
        val h = size.height - padT - padB

        // Independent ranges per series, widened to include the safe band.
        val temps = points.map { it.temperature }
        val beats = points.map { it.heartRate }
        val tMin = min(temps.min(), HealthRecord.MIN_TEMPERATURE) - 0.5f
        val tMax = max(temps.max(), HealthRecord.MAX_TEMPERATURE) + 0.5f
        val bMin = min(beats.min(), HealthRecord.MIN_HEART_RATE) - 5f
        val bMax = max(beats.max(), HealthRecord.MAX_HEART_RATE) + 5f

        // X by time fraction across the visible span (scaleType: time).
        val t0 = parseMillis(points.first().recordedAt).toFloat()
        val t1 = parseMillis(points.last().recordedAt).toFloat()
        val span = (t1 - t0).coerceAtLeast(1f)
        fun xOf(r: HealthRecord) = padL + (parseMillis(r.recordedAt) - t0) / span * w
        fun yOf(v: Float, lo: Float, hi: Float) = padT + (1f - (v - lo) / (hi - lo)) * h

        // Safe-range reference lines (dashed), one pair per axis.
        val dash = PathEffect.dashPathEffect(floatArrayOf(12f, 12f))
        fun refLine(v: Float, lo: Float, hi: Float, color: Color) {
            val y = yOf(v, lo, hi)
            drawLine(color.copy(alpha = 0.6f), Offset(padL, y), Offset(padL + w, y),
                strokeWidth = 2f, pathEffect = dash)
        }
        refLine(HealthRecord.MIN_TEMPERATURE, tMin, tMax, TempColor)
        refLine(HealthRecord.MAX_TEMPERATURE, tMin, tMax, TempColor)
        refLine(HealthRecord.MIN_HEART_RATE, bMin, bMax, BpmColor)
        refLine(HealthRecord.MAX_HEART_RATE, bMin, bMax, BpmColor)

        // Series polylines.
        drawSeries(points, ::xOf) { yOf(it.temperature, tMin, tMax) }.let {
            drawPath(it, TempColor, style = Stroke(width = 4f))
        }
        drawSeries(points, ::xOf) { yOf(it.heartRate, bMin, bMax) }.let {
            drawPath(it, BpmColor, style = Stroke(width = 4f))
        }

        // Point markers when the series is short enough to read.
        if (points.size <= 60) {
            points.forEach { r ->
                drawCircle(TempColor, 5f, Offset(xOf(r), yOf(r.temperature, tMin, tMax)))
                drawCircle(BpmColor, 5f, Offset(xOf(r), yOf(r.heartRate, bMin, bMax)))
            }
        }
    }
}

private fun DrawScope.drawSeries(
    points: List<HealthRecord>,
    xOf: (HealthRecord) -> Float,
    yOf: (HealthRecord) -> Float
): Path = Path().apply {
    points.forEachIndexed { i, r ->
        val x = xOf(r); val y = yOf(r)
        if (i == 0) moveTo(x, y) else lineTo(x, y)
    }
}
