package pe.edu.upc.vacapp.subscription.domain.model

/** The user's current subscription state. */
data class Subscription(
    val plan: String,
    val status: String,
    val isPlusActive: Boolean,
    val startDate: String? = null,
    val nextRenewal: String? = null,
    val includedCollars: Int = 0,
    val additionalCollars: Int = 0,
    val monthlyCost: Double = 0.0
)

/** A plan offered on the pricing screen (Free / Plus). */
data class Plan(
    val name: String,
    val monthlyPrice: Double,
    val includedCollars: Int,
    val additionalCollarMonthly: Double,
    val features: List<String>
) {
    val isPlus: Boolean get() = name.equals("Plus", ignoreCase = true)
}
