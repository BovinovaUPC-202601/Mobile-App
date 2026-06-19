package pe.edu.upc.vacapp.inventory.data.model

import pe.edu.upc.vacapp.inventory.domain.model.Category

data class CategoryResponse(
    val id: Int,
    val name: String
) {
    fun toCategory(productCount: Int = 0): Category = Category(
        id = id,
        name = name,
        productCount = productCount
    )
}
