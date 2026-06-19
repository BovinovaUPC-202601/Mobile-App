package pe.edu.upc.vacapp.inventory.data.model

import pe.edu.upc.vacapp.inventory.domain.model.Category

data class CreateCategoryRequest(
    val name: String
) {
    companion object {
        fun fromCategory(category: Category): CreateCategoryRequest = CreateCategoryRequest(
            name = category.name
        )
    }
}
