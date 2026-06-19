package pe.edu.upc.vacapp.inventory.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pe.edu.upc.vacapp.inventory.data.repository.InventoryRepository
import pe.edu.upc.vacapp.inventory.domain.model.Category
import pe.edu.upc.vacapp.inventory.domain.model.Product

class InventoryViewModel(
    private val inventoryRepository: InventoryRepository
) : ViewModel() {
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isProductLoading = MutableStateFlow(false)
    val isProductLoading: StateFlow<Boolean> = _isProductLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _addSuccess = MutableStateFlow(false)
    val addSuccess: StateFlow<Boolean> = _addSuccess

    private val _deleteSuccess = MutableStateFlow(false)
    val deleteSuccess: StateFlow<Boolean> = _deleteSuccess

    fun getProducts() {
        viewModelScope.launch {
            _isProductLoading.value = true
            try {
                _products.value = inventoryRepository.getProducts()
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error al obtener productos"
            } finally {
                _isProductLoading.value = false
            }
        }
    }

    fun getCategories() {
        viewModelScope.launch {
            try {
                val categoryResponses = inventoryRepository.getCategories()
                val products = _products.value
                _categories.value = categoryResponses.map { catResp ->
                    val count = products.count { it.categoryId == catResp.id }
                    catResp.toCategory(count)
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error al obtener categorías"
            }
        }
    }

    fun refreshAll() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val products = inventoryRepository.getProducts()
                _products.value = products
                val categoryResponses = inventoryRepository.getCategories()
                _categories.value = categoryResponses.map { catResp ->
                    val count = products.count { it.categoryId == catResp.id }
                    catResp.toCategory(count)
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error al cargar inventario"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addProduct(product: Product) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                inventoryRepository.createProduct(product)
                _addSuccess.value = true
                refreshAll()
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error al crear producto"
                _addSuccess.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                inventoryRepository.updateProduct(product)
                _addSuccess.value = true
                refreshAll()
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error al actualizar producto"
                _addSuccess.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteProduct(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                inventoryRepository.deleteProduct(id)
                _deleteSuccess.value = true
                refreshAll()
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error al eliminar producto"
                _deleteSuccess.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addCategory(category: Category) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                inventoryRepository.createCategory(category)
                _addSuccess.value = true
                refreshAll()
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error al crear categoría"
                _addSuccess.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateCategory(category: Category) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                inventoryRepository.updateCategory(category)
                _addSuccess.value = true
                refreshAll()
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error al actualizar categoría"
                _addSuccess.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteCategory(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                inventoryRepository.deleteCategory(id)
                _deleteSuccess.value = true
                refreshAll()
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error al eliminar categoría"
                _deleteSuccess.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetAddSuccess() {
        _addSuccess.value = false
    }

    fun resetDeleteSuccess() {
        _deleteSuccess.value = false
    }

    fun resetErrorMessage() {
        _errorMessage.value = null
    }
}
