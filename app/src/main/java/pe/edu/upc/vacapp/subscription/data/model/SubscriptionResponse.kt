package pe.edu.upc.vacapp.subscription.data.model

import pe.edu.upc.vacapp.subscription.domain.model.Plan
import pe.edu.upc.vacapp.subscription.domain.model.Subscription

/*
GET /subscriptions/current  (also the body returned by POST /plus/activate)
{
  "plan": "Free" | "Plus",
  "status": "Active" | "Cancelled" | "Suspended" | ...,
  "startDate": "2026-06-12T17:06:23.119847" | null,
  "nextRenewal": "2026-07-12T17:06:23.119906" | null,
  "includedCollars": 3,
  "additionalCollars": 0,
  "monthlyCost": 149
}
Dates arrive as ISO date-time with offset, so they are kept as raw strings to
avoid clashing with the app-wide LocalDate adapter (which only parses plain dates).
*/
data class SubscriptionResponse(
    val plan: String,
    val status: String,
    val startDate: String? = null,
    val nextRenewal: String? = null,
    val includedCollars: Int = 0,
    val additionalCollars: Int = 0,
    val monthlyCost: Double = 0.0
) {
    fun toDomain(): Subscription = Subscription(
        plan = plan,
        status = status,
        // Effective Plus access mirrors the web client and the backend [RequiresPlus]:
        // only an ACTIVE Plus unlocks Plus features. A cancelled/suspended Plus is locked.
        isPlusActive = plan.equals("Plus", ignoreCase = true) &&
            status.equals("Active", ignoreCase = true),
        startDate = startDate,
        nextRenewal = nextRenewal,
        includedCollars = includedCollars,
        additionalCollars = additionalCollars,
        monthlyCost = monthlyCost
    )
}

/*
GET /subscriptions/plans -> array of:
{ "name": "Plus", "monthlyPrice": 149, "includedCollars": 3,
  "additionalCollarMonthly": 25, "features": ["IA", "IoT", ...] }
*/
data class PlanResponse(
    val name: String,
    val monthlyPrice: Double = 0.0,
    val includedCollars: Int = 0,
    val additionalCollarMonthly: Double = 0.0,
    val features: List<String> = emptyList()
) {
    fun toDomain(): Plan = Plan(
        name = name,
        monthlyPrice = monthlyPrice,
        includedCollars = includedCollars,
        additionalCollarMonthly = additionalCollarMonthly,
        features = features
    )
}

/* POST /subscriptions/cancel -> { "message": "Subscription cancelled", "status": "Cancelled" } */
data class CancelResponse(
    val message: String? = null,
    val status: String? = null
)
