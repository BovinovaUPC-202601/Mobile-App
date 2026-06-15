package pe.edu.upc.vacapp.subscription.data.remote

import pe.edu.upc.vacapp.subscription.data.model.CancelResponse
import pe.edu.upc.vacapp.subscription.data.model.CheckoutResponse
import pe.edu.upc.vacapp.subscription.data.model.PlanResponse
import pe.edu.upc.vacapp.subscription.data.model.SubscriptionResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface SubscriptionService {
    @GET("subscriptions/current")
    suspend fun getCurrent(): Response<SubscriptionResponse>

    @GET("subscriptions/plans")
    suspend fun getPlans(): Response<List<PlanResponse>>

    @POST("subscriptions/plus/activate")
    suspend fun activatePlus(): Response<SubscriptionResponse>

    /** Opens a checkout session for Plus; returns the URL carrying the session ref. */
    @POST("subscriptions/plus/checkout")
    suspend fun createPlusCheckout(): Response<CheckoutResponse>

    /** Opens a checkout session for an additional collar slot. */
    @POST("subscriptions/additional-collars/checkout")
    suspend fun createCollarCheckout(): Response<CheckoutResponse>

    /** Confirms a checkout after the simulated card form. */
    @POST("subscriptions/checkout/{sessionRef}/confirm")
    suspend fun confirmCheckout(@Path("sessionRef") sessionRef: String): Response<Unit>

    @POST("subscriptions/cancel")
    suspend fun cancel(): Response<CancelResponse>
}
