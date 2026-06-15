package pe.edu.upc.vacapp.collars.domain.model

import java.util.UUID

data class Collar(
    val id: Int,
    val deviceId: String,
    val bovineId: Int,
    val operationalStatus: String? = null,
    val lifecycleStatus: String? = null,
    val batteryLevel: Int? = null
)

/** Collar quota for the user's plan. Plus includes 3; Free has none (403). */
data class CollarCapacity(
    val active: Int = 0,
    val allowance: Int = 0,
    val available: Int = 0
)

/**
 * A positional collar number N is encoded inside a globally-unique deviceId —
 * `collar-{N}-{random}` — so the UI can render a friendly "Collar N" label. The
 * raw deviceId is also shown (with a copy button) once a collar is assigned, so
 * the rancher can flash that exact value into the ESP32. Mirrors the web client's
 * collar-id lib; the backend enforces UNIQUE device ids.
 */
object CollarId {
    private val DEVICE_ID_RE = Regex("""^collar-(\d+)-""")

    fun makeDeviceId(n: Int): String = "collar-$n-${UUID.randomUUID().toString().take(8)}"

    fun parseNumber(deviceId: String): Int? =
        DEVICE_ID_RE.find(deviceId)?.groupValues?.get(1)?.toIntOrNull()

    fun label(deviceId: String): String =
        parseNumber(deviceId)?.let { "Collar $it" } ?: "Collar"
}
