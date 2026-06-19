package pe.edu.upc.vacapp.inventory.data.remote

import pe.edu.upc.vacapp.inventory.data.model.CategoryResponse
import pe.edu.upc.vacapp.inventory.data.model.CreateCategoryRequest
import pe.edu.upc.vacapp.inventory.data.model.CreateProductRequest
import pe.edu.upc.vacapp.inventory.data.model.ProductResponse
import pe.edu.upc.vacapp.inventory.data.model.UpdateCategoryRequest
import pe.edu.upc.vacapp.inventory.data.model.UpdateProductRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface InventoryService {
    @GET("inventory/products")
    suspend fun getProducts(): Response<List<ProductResponse>>

    @POST("inventory/products")
    suspend fun createProduct(
        @Body product: CreateProductRequest
    ): Response<ProductResponse>

    @PUT("inventory/products/{id}")
    suspend fun updateProduct(
        @Path("id") id: Int,
        @Body product: UpdateProductRequest
    ): Response<ProductResponse>

    @DELETE("inventory/products/{id}")
    suspend fun deleteProduct(
        @Path("id") id: Int
    ): Response<Any>

    @GET("inventory/categories")
    suspend fun getCategories(): Response<List<CategoryResponse>>

    @POST("inventory/categories")
    suspend fun createCategory(
        @Body category: CreateCategoryRequest
    ): Response<CategoryResponse>

    @PUT("inventory/categories/{id}")
    suspend fun updateCategory(
        @Path("id") id: Int,
        @Body category: UpdateCategoryRequest
    ): Response<CategoryResponse>

    @DELETE("inventory/categories/{id}")
    suspend fun deleteCategory(
        @Path("id") id: Int
    ): Response<Any>
}
