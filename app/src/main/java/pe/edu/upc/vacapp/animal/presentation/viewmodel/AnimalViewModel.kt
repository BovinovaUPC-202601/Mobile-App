package pe.edu.upc.vacapp.animal.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pe.edu.upc.vacapp.animal.data.repository.AnimalRepository
import pe.edu.upc.vacapp.animal.domain.model.Animal
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
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

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
                animalRepository.addAnimal(animal)
                _addAnimalSuccess.value = true  // <-- Success
            } catch (e: IllegalArgumentException) {
                _errorMessage.value = e.message ?: "Error adding animal"
            } catch (e: Exception) {
                _errorMessage.value = "Unknown error when adding the animal"
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
    fun getAllAnimals() {
        viewModelScope.launch {
            _animals.value = animalRepository.getAllAnimals()
        }
    }
    //
    fun getBarns() {
        viewModelScope.launch {
            _barns.value = animalRepository.getBarns()
        }
    }
}