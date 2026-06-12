package pe.edu.upc.vacapp.subscription.data.remote

import pe.edu.upc.vacapp.subscription.data.model.CancelResponse
import pe.edu.upc.vacapp.subscription.data.model.PlanResponse
import pe.edu.upc.vacapp.subscription.data.model.SubscriptionResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST

interface SubscriptionService {
    @GET("subscriptions/current")
    suspend fun getCurrent(): Response<SubscriptionResponse>

    @GET("subscriptions/plans")
    suspend fun getPlans(): Response<List<PlanResponse>>

    @POST("subscriptions/plus/activate")
    suspend fun activatePlus(): Response<SubscriptionResponse>

    @POST("subscriptions/cancel")
    suspend fun cancel(): Response<CancelResponse>
}
