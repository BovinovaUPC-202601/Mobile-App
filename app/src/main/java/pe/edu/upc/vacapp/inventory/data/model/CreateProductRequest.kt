package pe.edu.upc.vacapp.inventory.data.model

import pe.edu.upc.vacapp.inventory.domain.model.Product

data class CreateProductRequest(
    val name: String,
    val categoryId: Int,
    val quantity: Int,
    val expirationDate: String?,
    val unit: String?
) {
    companion object {
        fun fromProduct(product: Product): CreateProductRequest = CreateProductRequest(
            name = product.name,
            categoryId = product.categoryId,
            quantity = product.quantity,
            expirationDate = product.expirationDate,
            unit = product.unit
        )
    }
}
