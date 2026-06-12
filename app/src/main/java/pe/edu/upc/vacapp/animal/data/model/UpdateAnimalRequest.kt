package pe.edu.upc.vacapp.animal.data.model

import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import pe.edu.upc.vacapp.animal.domain.model.Animal

/**
 * JSON body for PUT /bovines/{id}. Unlike creation, the update is sent as plain
 * JSON (no image upload), mirroring the web client's updateAnimal. Used to edit
 * the per-bovine biometric thresholds that drive the IoT alerts.
 */
data class UpdateAnimalRequest(
    val name: String,
    val gender: String,
    val birthDate: String,
    val breed: String,
    val stableId: Int,
    val minTemperature: Double,
    val maxTemperature: Double,
    val minHeartRate: Int,
    val maxHeartRate: Int
) {
    companion object {
        fun fromAnimal(animal: Animal): UpdateAnimalRequest {
            // Domain keeps birthDate as dd/MM/yyyy; the backend expects ISO yyyy-MM-dd.
            val isoDate = LocalDate
                .parse(animal.birthDate, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                .toString()

            return UpdateAnimalRequest(
                name = animal.name,
                gender = if (animal.isMale) "male" else "female",
                birthDate = isoDate,
                breed = animal.breed,
                stableId = animal.barnId,
                minTemperature = animal.minTemperature,
                maxTemperature = animal.maxTemperature,
                minHeartRate = animal.minHeartRate,
                maxHeartRate = animal.maxHeartRate
            )
        }
    }
}
