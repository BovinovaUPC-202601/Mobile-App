package pe.edu.upc.vacapp.campaign.domain.model

import org.threeten.bp.LocalDate
import pe.edu.upc.vacapp.shared.util.DateUtils

data class Campaign(
    val name: String = "",
    val description: String = "",
    val startDate: LocalDate = DateUtils.today(),
    val endDate:  LocalDate = DateUtils.today(),
)
