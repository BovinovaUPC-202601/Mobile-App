package pe.edu.upc.vacapp.animal.domain.model

data class Breed(
    val id: Int,
    val name: String,
    val minTemperature: Double,
    val maxTemperature: Double,
    val minHeartRate: Int,
    val maxHeartRate: Int
)
