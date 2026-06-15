package pe.edu.upc.vacapp.subscription.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pe.edu.upc.vacapp.subscription.data.remote.SubscriptionService
import pe.edu.upc.vacapp.subscription.domain.model.Plan
import pe.edu.upc.vacapp.subscription.domain.model.Subscription
import retrofit2.Response

/** The session token is missing or expired (401). */
class SubscriptionSessionExpiredException(
    message: String = "Your session expired. Please sign in again."
) : Exception(message)

/**
 * Talks to the subscriptions backend.
 *
 * Open so it can be substituted by a fake in tests; production wiring uses
 * [SubscriptionService] over Retrofit.
 */
open class SubscriptionRepository(
    private val service: SubscriptionService
) {
    open suspend fun getCurrent(): Subscription = withContext(Dispatchers.IO) {
        unwrap(service.getCurrent(), "loading the current subscription").toDomain()
    }

    open suspend fun getPlans(): List<Plan> = withContext(Dispatchers.IO) {
        unwrap(service.getPlans(), "loading the available plans").map { it.toDomain() }
    }

    open suspend fun activatePlus(): Subscription = withContext(Dispatchers.IO) {
        unwrap(service.activatePlus(), "activating the Plus plan").toDomain()
    }

    /** Cancels Plus; the user falls back to Free. */
    open suspend fun cancel(): Unit = withContext(Dispatchers.IO) {
        unwrap(service.cancel(), "cancelling the subscription")
    }

    private fun <T> unwrap(response: Response<T>, action: String): T {
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Empty response when $action")
        }
        if (response.code() == 401) throw SubscriptionSessionExpiredException()

        val error = response.errorBody()?.string().orEmpty()
        throw Exception("Error $action: ${error.ifBlank { response.code().toString() }}")
    }
}
