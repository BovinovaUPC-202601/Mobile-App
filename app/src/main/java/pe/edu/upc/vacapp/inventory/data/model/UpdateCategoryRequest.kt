package pe.edu.upc.vacapp.inventory.data.model

import pe.edu.upc.vacapp.inventory.domain.model.Category

data class UpdateCategoryRequest(
    val name: String
) {
    companion object {
        fun fromCategory(category: Category): UpdateCategoryRequest = UpdateCategoryRequest(
            name = category.name
        )
    }
}
