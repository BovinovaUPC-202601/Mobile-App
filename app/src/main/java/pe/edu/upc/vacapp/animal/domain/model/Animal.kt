package pe.edu.upc.vacapp.animal.domain.model

data class Animal(
    val id: Int = 0,
    val name: String = "",
    val breed: String = "",
    val age: Int = 0,
    val birthDate: String = "",
    val barnId: Int = 0,
    val barnName: String = "",
    val image: AnimalImage? = null,
    val isMale: Boolean = true,
    val minTemperature: Double = 38.0,
    val maxTemperature: Double = 39.3,
    val minHeartRate: Int = 40,
    val maxHeartRate: Int = 80
)