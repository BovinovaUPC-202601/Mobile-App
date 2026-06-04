package pe.edu.upc.vacapp.campaign.domain.model

import org.threeten.bp.LocalDate

data class Campaign(
    val name: String = "",
    val description: String = "",
    val startDate: LocalDate = LocalDate.now(),
    val endDate:  LocalDate = LocalDate.now(),
    val barnId: Int = 0,
)
