package pe.edu.upc.vacapp.shared.util

import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId

object DateUtils {
    val PERU_ZONE: ZoneId = ZoneId.of("America/Lima")
    fun today(): LocalDate = LocalDate.now(PERU_ZONE)
}
