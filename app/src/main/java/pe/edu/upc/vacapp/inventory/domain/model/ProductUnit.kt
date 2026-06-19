package pe.edu.upc.vacapp.inventory.domain.model

object ProductUnit {
    val ALL = listOf(
        "kg" to "kg (kilogramo)",
        "g" to "g (gramo)",
        "lb" to "lb (libra)",
        "L" to "L (litro)",
        "mL" to "mL (mililitro)",
        "unid" to "unid (unidad)",
        "caja" to "caja",
        "saco" to "saco",
        "gal" to "gal (galón)",
        "m" to "m (metro)",
    )

    fun displayValue(value: String?): String =
        ALL.firstOrNull { it.first == value }?.second ?: value ?: ""
}
