package pe.edu.upc.vacapp.animal.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pe.edu.upc.vacapp.animal.data.model.BreedRequest
import pe.edu.upc.vacapp.animal.data.repository.AnimalRepository
import pe.edu.upc.vacapp.animal.domain.model.Animal
import pe.edu.upc.vacapp.animal.domain.model.Breed
import pe.edu.upc.vacapp.barn.domain.model.Barn

class AnimalViewModel(
    private val animalRepository: AnimalRepository
) : ViewModel() {

    /* Declaration */
    //
    private val _animals = MutableStateFlow<List<Animal>>(emptyList())
    val animals: StateFlow<List<Animal>> = _animals
    //
    private val _barns = MutableStateFlow<List<Barn>>(emptyList())
    val barn: StateFlow<List<Barn>> = _barns
    //
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage
    //
    private val _addAnimalSuccess = MutableStateFlow(false)
    val addAnimalSuccess: StateFlow<Boolean> = _addAnimalSuccess
    //
    private val _deleteSuccess = MutableStateFlow(false)
    val deleteSuccess: StateFlow<Boolean> = _deleteSuccess
    //
    private val _updateSuccess = MutableStateFlow(false)
    val updateSuccess: StateFlow<Boolean> = _updateSuccess
    private val _breeds = MutableStateFlow<List<Breed>>(emptyList())
    val breeds: StateFlow<List<Breed>> = _breeds
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    //
    private val _breedError = MutableStateFlow<String?>(null)
    val breedError: StateFlow<String?> = _breedError

    /* Methods */
    //
    fun clearErrorMessage() {
        _errorMessage.value = null
    }
    //
    fun addAnimal(animal: Animal) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val barn = _barns.value.find { it.id == animal.barnId }
                val limit = barn?.limit?.toIntOrNull()
                if (limit != null && limit > 0) {
                    val currentCount = _animals.value.count { it.barnId == animal.barnId }
                    if (currentCount >= limit) {
                        throw IllegalArgumentException(
                            "El establo \"${barn.name}\" ha alcanzado su capacidad máxima ($limit)."
                        )
                    }
                }
                animalRepository.addAnimal(animal)
                _addAnimalSuccess.value = true
            } catch (e: IllegalArgumentException) {
                _errorMessage.value = e.message ?: "Error al añadir animal"
            } catch (e: Exception) {
                _errorMessage.value = "Error desconocido al añadir el animal"
            } finally {
                _isLoading.value = false
            }
        }
    }
    //
    fun clearAddAnimalSuccess() {
        _addAnimalSuccess.value = false
    }
    //
    fun deleteAnimal(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                animalRepository.deleteAnimal(id)
                _deleteSuccess.value = true
                getAllAnimals()
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error al eliminar animal"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearDeleteSuccess() {
        _deleteSuccess.value = false
    }
    //
    fun getAllAnimals() {
        viewModelScope.launch {
            try {
                _animals.value = animalRepository.getAllAnimals()
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error al obtener animales"
            }
        }
    }
    //
    fun getBarns() {
        viewModelScope.launch {
            try {
                _barns.value = animalRepository.getBarns()
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error al obtener establos"
            }
        }
    }

    fun getBreeds() {
        viewModelScope.launch {
            try {
                _breeds.value = animalRepository.getBreeds()
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error al obtener razas"
            }
        }
    }

    // Emits the freshly-updated animal so the detail screen can reflect new thresholds.
    private val _updatedAnimal = MutableStateFlow<Animal?>(null)
    val updatedAnimal: StateFlow<Animal?> = _updatedAnimal

    /** Persists edits to an existing bovine (e.g. its biometric thresholds). */
    fun updateAnimal(animal: Animal) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _updatedAnimal.value = animalRepository.updateAnimal(animal)
                _updateSuccess.value = true
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error al actualizar animal"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearUpdatedAnimal() {
        _updatedAnimal.value = null
    }

    fun clearUpdateSuccess() {
        _updateSuccess.value = false
    }

    fun clearBreedError() {
        _breedError.value = null
    }

    fun createBreed(request: BreedRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            _breedError.value = null
            try {
                animalRepository.createBreed(request)
                getBreeds()
            } catch (e: Exception) {
                _breedError.value = e.message ?: "Error al crear raza"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateBreed(id: Int, request: BreedRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            _breedError.value = null
            try {
                animalRepository.updateBreed(id, request)
                getBreeds()
            } catch (e: Exception) {
                _breedError.value = e.message ?: "Error al actualizar raza"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteBreed(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _breedError.value = null
            try {
                animalRepository.deleteBreed(id)
                getBreeds()
            } catch (e: Exception) {
                _breedError.value = e.message ?: "Error al eliminar raza"
            } finally {
                _isLoading.value = false
            }
        }
    }
}