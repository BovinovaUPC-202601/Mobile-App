package pe.edu.upc.vacapp.animal.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pe.edu.upc.vacapp.Vacapp
import pe.edu.upc.vacapp.animal.data.model.AddAnimalRequest
import pe.edu.upc.vacapp.animal.data.model.BreedRequest
import pe.edu.upc.vacapp.animal.data.model.UpdateAnimalRequest
import pe.edu.upc.vacapp.animal.data.model.toMultipartPart
import pe.edu.upc.vacapp.animal.data.model.toRequestBody
import pe.edu.upc.vacapp.animal.data.remote.AnimalService
import pe.edu.upc.vacapp.animal.domain.model.Animal
import pe.edu.upc.vacapp.animal.domain.model.Breed
import pe.edu.upc.vacapp.barn.domain.model.Barn
import pe.edu.upc.vacapp.shared.data.remote.errorMessage
import java.io.File

open class AnimalRepository(
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
            throw Exception("Error al añadir animal: ${res.errorBody()?.string()}")
        }
    }

    /** Updates an existing bovine (used to edit its biometric thresholds). */
    open suspend fun updateAnimal(animal: Animal): Animal = withContext(Dispatchers.IO) {
        val res = animalService.updateAnimal(animal.id, UpdateAnimalRequest.fromAnimal(animal))
        if (res.isSuccessful) {
            res.body()?.toAnimal() ?: animal
        } else {
            throw Exception("Error al actualizar animal: ${res.errorBody()?.string()}")
        }
    }

    open suspend fun getAllAnimals(): List<Animal> = withContext(Dispatchers.IO) {
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
            throw Exception("Error al obtener animales: ${res.errorBody()?.string()}")
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

    suspend fun getBreeds(): List<Breed> = withContext(Dispatchers.IO){
        val response = animalService.getBreeds()

        if (response.isSuccessful) {
            return@withContext response.body()?.map {
                it.toBreed()
            } ?: emptyList()
        }

        return@withContext emptyList()
    }

    suspend fun deleteAnimal(id: Int) = withContext(Dispatchers.IO) {
        val res = animalService.deleteAnimal(id)
        if (!res.isSuccessful) {
            throw Exception(res.errorMessage())
        }
    }

    suspend fun createBreed(request: BreedRequest): Breed = withContext(Dispatchers.IO) {
        val res = animalService.createBreed(request)
        if (res.isSuccessful) {
            res.body()?.toBreed() ?: throw Exception("Error al crear raza")
        } else {
            throw Exception(res.errorMessage())
        }
    }

    suspend fun updateBreed(id: Int, request: BreedRequest): Breed = withContext(Dispatchers.IO) {
        val res = animalService.updateBreed(id, request)
        if (res.isSuccessful) {
            res.body()?.toBreed() ?: throw Exception("Error al actualizar raza")
        } else {
            throw Exception(res.errorMessage())
        }
    }

    suspend fun deleteBreed(id: Int) = withContext(Dispatchers.IO) {
        val res = animalService.deleteBreed(id)
        if (!res.isSuccessful) {
            throw Exception(res.errorMessage())
        }
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