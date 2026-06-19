package pe.edu.upc.vacapp.inventory.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pe.edu.upc.vacapp.inventory.data.model.CategoryResponse
import pe.edu.upc.vacapp.inventory.data.model.CreateCategoryRequest
import pe.edu.upc.vacapp.inventory.data.model.CreateProductRequest
import pe.edu.upc.vacapp.inventory.data.model.ProductResponse
import pe.edu.upc.vacapp.inventory.data.model.UpdateCategoryRequest
import pe.edu.upc.vacapp.inventory.data.model.UpdateProductRequest
import pe.edu.upc.vacapp.inventory.data.remote.InventoryService
import pe.edu.upc.vacapp.inventory.domain.model.Category
import pe.edu.upc.vacapp.inventory.domain.model.Product
import pe.edu.upc.vacapp.shared.data.remote.errorMessage

class InventoryRepository(
    private val inventoryService: InventoryService
) {
    suspend fun getProducts(): List<Product> = withContext(Dispatchers.IO) {
        val response = inventoryService.getProducts()
        if (response.isSuccessful) {
            response.body()?.map { it.toProduct() } ?: emptyList()
        } else {
            throw Exception(response.errorMessage())
        }
    }

    suspend fun createProduct(product: Product): Product? = withContext(Dispatchers.IO) {
        val request = CreateProductRequest.fromProduct(product)
        val response = inventoryService.createProduct(request)
        if (response.isSuccessful) {
            response.body()?.toProduct()
        } else {
            throw Exception(response.errorMessage())
        }
    }

    suspend fun updateProduct(product: Product): Product? = withContext(Dispatchers.IO) {
        val request = UpdateProductRequest.fromProduct(product)
        val response = inventoryService.updateProduct(product.id, request)
        if (response.isSuccessful) {
            response.body()?.toProduct()
        } else {
            throw Exception(response.errorMessage())
        }
    }

    suspend fun deleteProduct(id: Int) = withContext(Dispatchers.IO) {
        val response = inventoryService.deleteProduct(id)
        if (!response.isSuccessful) {
            throw Exception(response.errorMessage())
        }
    }

    suspend fun getCategories(): List<CategoryResponse> = withContext(Dispatchers.IO) {
        val response = inventoryService.getCategories()
        if (response.isSuccessful) {
            response.body() ?: emptyList()
        } else {
            throw Exception(response.errorMessage())
        }
    }

    suspend fun createCategory(category: Category): Category? = withContext(Dispatchers.IO) {
        val request = CreateCategoryRequest.fromCategory(category)
        val response = inventoryService.createCategory(request)
        if (response.isSuccessful) {
            response.body()?.toCategory()
        } else {
            throw Exception(response.errorMessage())
        }
    }

    suspend fun updateCategory(category: Category): Category? = withContext(Dispatchers.IO) {
        val request = UpdateCategoryRequest.fromCategory(category)
        val response = inventoryService.updateCategory(category.id, request)
        if (response.isSuccessful) {
            response.body()?.toCategory()
        } else {
            throw Exception(response.errorMessage())
        }
    }

    suspend fun deleteCategory(id: Int) = withContext(Dispatchers.IO) {
        val response = inventoryService.deleteCategory(id)
        if (!response.isSuccessful) {
            throw Exception(response.errorMessage())
        }
    }
}
