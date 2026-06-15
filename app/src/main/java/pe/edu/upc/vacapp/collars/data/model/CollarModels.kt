package pe.edu.upc.vacapp.collars.data.model

import pe.edu.upc.vacapp.collars.domain.model.Collar
import pe.edu.upc.vacapp.collars.domain.model.CollarCapacity

/*
GET /iot-monitoring/collars -> [ {
  id, deviceId, bovineId, operationalStatus, lifecycleStatus,
  lastTemperature, lastHeartRate, batteryLevel, lastSeenAt, registeredAt } ]
*/
data class CollarResponse(
    val id: Int,
    val deviceId: String,
    val bovineId: Int,
    val operationalStatus: String? = null,
    val lifecycleStatus: String? = null,
    val batteryLevel: Int? = null
) {
    fun toDomain(): Collar = Collar(
        id = id,
        deviceId = deviceId,
        bovineId = bovineId,
        operationalStatus = operationalStatus,
        lifecycleStatus = lifecycleStatus,
        batteryLevel = batteryLevel
    )
}

/* GET /iot-monitoring/collars/capacity -> { active, allowance, available } */
data class CapacityResponse(
    val active: Int = 0,
    val allowance: Int = 0,
    val available: Int = 0
) {
    fun toDomain(): CollarCapacity = CollarCapacity(active, allowance, available)
}

/* POST body */
data class RegisterCollarRequest(
    val deviceId: String,
    val bovineId: Int
)

/* PUT /{id} body (reassign to another bovine) */
data class ReassignCollarRequest(
    val bovineId: Int
)
