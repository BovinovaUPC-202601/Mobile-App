package pe.edu.upc.vacapp.subscription.domain.model

/**
 * A checkout opened against the (mock) payment gateway. Mirrors the web flow:
 * the backend creates the session, the client collects a (fake) card and then
 * confirms it by [sessionRef].
 */
data class CheckoutSession(
    val sessionRef: String,
    val concept: String,   // "PlusMonthly" | "AdditionalCollar"
    val amount: String     // monthly price, e.g. "149"
) {
    val isPlus: Boolean get() = concept == "PlusMonthly"
    val label: String get() = if (isPlus) "Plan Plus" else "Collar adicional"
}
