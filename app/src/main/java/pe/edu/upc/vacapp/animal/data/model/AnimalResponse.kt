package pe.edu.upc.vacapp.animal.data.model

import android.annotation.SuppressLint
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import pe.edu.upc.vacapp.animal.domain.model.Animal
import pe.edu.upc.vacapp.animal.domain.model.AnimalImage

data class AnimalResponse(
    val id: Int,
    val name: String,
    val gender: String,
    val birthDate: String,
    val breed: String,
    val location: String?,
    val bovineImg: String?,
    val stableId: Int
) {
    @SuppressLint("DefaultLocale")
    fun toAnimal(): Animal {
        val birthDateOnly = try {
            LocalDate.parse(birthDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        } catch (e: Exception) {
            try {
                LocalDateTime.parse(birthDate, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"))
                    .toLocalDate()
            } catch (e: Exception) {
                LocalDateTime.parse(birthDate, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))
                    .toLocalDate()
            }
        }

        val formattedDate = birthDateOnly.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        val today = LocalDate.now()

        val age = if ((today.monthValue > birthDateOnly.monthValue) ||
            (today.monthValue == birthDateOnly.monthValue && today.dayOfMonth >= birthDateOnly.dayOfMonth)
        ) {
            today.year - birthDateOnly.year
        } else {
            today.year - birthDateOnly.year - 1
        }

        return Animal(
            id = id,
            name = name,
            breed = breed,
            //TODO: Implement weight calculation or fetch from API (falta en el backend)
            //weight = ,
            age = age,
            birthDate = formattedDate,
            barnId = stableId,
            location = location.orEmpty(),
            image = AnimalImage.FromUrl(bovineImg.orEmpty()),
            isMale = gender == "male"
        )
    }
}
