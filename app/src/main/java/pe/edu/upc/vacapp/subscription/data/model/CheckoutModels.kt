package pe.edu.upc.vacapp.subscription.data.model

import pe.edu.upc.vacapp.subscription.domain.model.CheckoutSession

/*
POST /subscriptions/plus/checkout
POST /subscriptions/additional-collars/checkout
-> { "checkoutUrl": "http://.../checkout?session=mock_sess_xxx&concept=PlusMonthly&amount=149" }

The backend returns a hosted-checkout URL. The mobile app does NOT open it; it
parses the query (session / concept / amount) and drives a native card screen,
then confirms against POST /subscriptions/checkout/{sessionRef}/confirm.
*/
data class CheckoutResponse(
    val checkoutUrl: String
) {
    fun toDomain(): CheckoutSession {
        val query = checkoutUrl.substringAfter('?', "")
        val params = query.split('&')
            .mapNotNull { part ->
                val idx = part.indexOf('=')
                if (idx <= 0) null else part.substring(0, idx) to part.substring(idx + 1)
            }
            .toMap()

        return CheckoutSession(
            sessionRef = params["session"].orEmpty(),
            concept    = params["concept"] ?: "PlusMonthly",
            amount     = params["amount"] ?: "0"
        )
    }
}
