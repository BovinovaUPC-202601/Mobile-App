package pe.edu.upc.vacapp.animal.domain.model

data class Animal(
    val id: Int = 0,
    val name: String = "",
    val breed: String = "",
    val age: Int = 0,
    val birthDate: String = "",
    val barnId: Int = 0,
    val barnName: String = "",
    val image: AnimalImage? = null,
    val isMale: Boolean = true
)