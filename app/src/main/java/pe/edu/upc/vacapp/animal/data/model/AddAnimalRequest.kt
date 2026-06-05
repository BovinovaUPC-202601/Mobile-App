package pe.edu.upc.vacapp.animal.data.model

import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import pe.edu.upc.vacapp.animal.domain.model.Animal
import pe.edu.upc.vacapp.animal.domain.model.AnimalImage
import java.io.File

data class AddAnimalRequest(
    val name: String,
    val gender: String,
    val birthDate: String,
    val breed: String,
    val stableId: Int,
    val image: File,
    val minTemperature: Double,
    val maxTemperature: Double,
    val minHeartRate: Int,
    val maxHeartRate: Int
) {
    companion object {
        fun fromAnimal(animal: Animal): AddAnimalRequest {
            val file = (animal.image as? AnimalImage.FromFile)?.file
                ?: throw IllegalArgumentException("Animal must have a local image file to upload")

            val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val formatedDate = LocalDate.parse(animal.birthDate, dateFormatter)

            return AddAnimalRequest(
                animal.name,
                if (animal.isMale) "male" else "female",
                formatedDate.toString(),
                animal.breed,
                animal.barnId,
                file,
                animal.minTemperature,
                animal.maxTemperature,
                animal.minHeartRate,
                animal.maxHeartRate
            )

        }
    }
}