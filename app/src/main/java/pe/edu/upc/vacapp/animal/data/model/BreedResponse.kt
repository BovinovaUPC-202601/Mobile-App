package pe.edu.upc.vacapp.animal.data.model

import pe.edu.upc.vacapp.animal.domain.model.Breed

data class BreedResponse(
    val id: Int,
    val name: String,
    val minTemperature: Double,
    val maxTemperature: Double,
    val minHeartRate: Int,
    val maxHeartRate: Int,
    val userId: Int? = null
){
    fun toBreed(): Breed {
        return Breed(
            id = id,
            name = name,
            minTemperature = minTemperature,
            maxTemperature = maxTemperature,
            minHeartRate = minHeartRate,
            maxHeartRate = maxHeartRate,
            userId = userId
        )
    }
}
