package pe.edu.upc.vacapp.inventory.domain.model

data class Product(
    val id: Int = 0,
    val name: String = "",
    val categoryId: Int = 0,
    val categoryName: String = "",
    val quantity: Int = 0,
    val unit: String? = null,
    val expirationDate: String? = null
)
