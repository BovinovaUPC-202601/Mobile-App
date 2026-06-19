package pe.edu.upc.vacapp.animal.data.model

data class BreedRequest(
    val name: String,
    val minTemperature: Double,
    val maxTemperature: Double,
    val minHeartRate: Int,
    val maxHeartRate: Int
)
