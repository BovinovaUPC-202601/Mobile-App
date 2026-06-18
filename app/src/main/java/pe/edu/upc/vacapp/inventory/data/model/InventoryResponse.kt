package pe.edu.upc.vacapp.inventory.data.model

import android.annotation.SuppressLint
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import pe.edu.upc.vacapp.inventory.domain.model.Inventory
import pe.edu.upc.vacapp.inventory.domain.model.InventoryImage
import pe.edu.upc.vacapp.shared.util.DateUtils


data class InventoryResponse(
    val id: Int,
    val name: String,
    val vaccineType: String,
    val vaccineDate: String,
    val bovineId: Int,
    val vaccineImg: String
) {
    @SuppressLint("DefaultLocale")
    fun toInventory(): Inventory {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss[.SSS][.SS][.S]")
        val localDateTime = LocalDateTime.parse(vaccineDate, formatter)

        val vaccineDateOnly = localDateTime.toLocalDate()
        val formattedDate = vaccineDateOnly.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        val today = DateUtils.today()

        val age = if ((today.monthValue > vaccineDateOnly.monthValue) ||
            (today.monthValue == vaccineDateOnly.monthValue && today.dayOfMonth >= vaccineDateOnly.dayOfMonth)
        ) {
            today.year - vaccineDateOnly.year
        } else {
            today.year - vaccineDateOnly.year - 1
        }

        return Inventory(
            id = id,
            name = name,
            vaccineType = vaccineType,
            vaccineDate = formattedDate,
            bovineId = bovineId,
            image = InventoryImage.FromUrl(vaccineImg)
        )
    }
}
