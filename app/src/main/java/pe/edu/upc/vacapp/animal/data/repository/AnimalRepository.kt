package pe.edu.upc.vacapp.animal.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pe.edu.upc.vacapp.Vacapp
import pe.edu.upc.vacapp.animal.data.model.AddAnimalRequest
import pe.edu.upc.vacapp.animal.data.model.toMultipartPart
import pe.edu.upc.vacapp.animal.data.model.toRequestBody
import pe.edu.upc.vacapp.animal.data.remote.AnimalService
import pe.edu.upc.vacapp.animal.domain.model.Animal
import pe.edu.upc.vacapp.barn.domain.model.Barn
import java.io.File

class AnimalRepository(
    private val animalService: AnimalService
) {
    suspend fun addAnimal(animal: Animal) = withContext(Dispatchers.IO) {
        //TODO: save in ROOM
        val req = AddAnimalRequest.fromAnimal(animal)

        val res = animalService.addAnimal(
            req.name.toRequestBody(),
            req.gender.toRequestBody(),
            req.birthDate.toRequestBody(),
            req.breed.toRequestBody(),
            req.stableId.toRequestBody(),
            req.minTemperature.toRequestBody(),
            req.maxTemperature.toRequestBody(),
            req.minHeartRate.toRequestBody(),
            req.maxHeartRate.toRequestBody(),
            req.image.toMultipartPart("FileData"),
        )

        if (res.isSuccessful) {
            //val imgInternalPath = copyFileToInternalStorage(req.image)?.absolutePath ?: ""
            res.body()
        } else {
            throw Exception("Error adding animal: ${res.errorBody()?.string()}")
        }
    }

    suspend fun getAllAnimals(): List<Animal> = withContext(Dispatchers.IO) {
        val res = animalService.getAllAnimals()

        if (res.isSuccessful) {
            val animalsResponse = res.body() ?: emptyList()

            val barns = getBarns()

            animalsResponse.map { animalDto ->
                val animal = animalDto.toAnimal()

                val matchingBarnName = barns.find { it.id == animal.barnId }?.name ?: "Sin Establo"

                animal.copy(barnName = matchingBarnName)
            }
        } else {
            throw Exception("Error fetching animals: ${res.errorBody()?.string()}")
        }
    }

    suspend fun getBarns(): List<Barn> = withContext(Dispatchers.IO) {
        val response = animalService.getBarns()

        if (response.isSuccessful) {
            return@withContext response.body()?.map {
                it.toBarn()
            } ?: emptyList()
        }

        return@withContext emptyList()
    }

    suspend fun copyFileToInternalStorage(sourceFile: File): File? = withContext(Dispatchers.IO) {
        val context = Vacapp.instance.applicationContext

        return@withContext try {
            val destFile = File(context.filesDir, "animal_${System.currentTimeMillis()}.jpg")
            sourceFile.inputStream().use { input ->
                destFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            destFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}