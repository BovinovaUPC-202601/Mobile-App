package pe.edu.upc.vacapp.inventory.data.model

import pe.edu.upc.vacapp.inventory.domain.model.Product

data class ProductResponse(
    val id: Int,
    val name: String,
    val categoryId: Int,
    val categoryName: String? = null,
    val quantity: Int,
    val unit: String?,
    val expirationDate: String?
) {
    fun toProduct(): Product = Product(
        id = id,
        name = name,
        categoryId = categoryId,
        categoryName = categoryName ?: "",
        quantity = quantity,
        unit = unit,
        expirationDate = expirationDate
    )
}
