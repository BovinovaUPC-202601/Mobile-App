package pe.edu.upc.vacapp.ai

import com.google.gson.GsonBuilder
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Test
import pe.edu.upc.vacapp.ai.data.model.BovineChatRequest
import pe.edu.upc.vacapp.ai.data.model.GeneralChatRequest
import pe.edu.upc.vacapp.ai.data.remote.AiAssistantService
import pe.edu.upc.vacapp.iam.data.model.LoginRequest
import pe.edu.upc.vacapp.iam.data.remote.AuthService
import pe.edu.upc.vacapp.shared.data.remote.ApiConstants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Live contract tests against the deployed backend ([ApiConstants.BASE_URL]).
 *
 * These are opt-in so the normal unit-test run does not hit the network or the AI provider.
 * Enable them by setting environment variables:
 *
 *   VACAPP_IT=1                          (required to run any of these)
 *   VACAPP_TOKEN=<jwt>                   (a Plus user's token), OR
 *   VACAPP_EMAIL / VACAPP_PASSWORD       (credentials to sign in and obtain the token)
 *   VACAPP_BOVINE_ID=<id>                (a bovine owned by that user, for the bovine tests)
 *
 * The AI Assistant is gated behind the Plus plan: a Free token yields HTTP 403, in which case
 * the affected test is skipped rather than failed.
 */
class AiAssistantIntegrationTest {

    private val gson = GsonBuilder().create()

    private fun retrofit(token: String?): Retrofit {
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val builder = chain.request().newBuilder()
                if (!token.isNullOrBlank()) builder.addHeader("Authorization", "Bearer $token")
                chain.proceed(builder.build())
            }
            .build()

        return Retrofit.Builder()
            .baseUrl(ApiConstants.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    private fun aiService(token: String?): AiAssistantService =
        retrofit(token).create(AiAssistantService::class.java)

    private fun assumeIntegrationEnabled() {
        assumeTrue("Set VACAPP_IT=1 to run live integration tests.", System.getenv("VACAPP_IT") == "1")
    }

    private fun resolveToken(): String? {
        System.getenv("VACAPP_TOKEN")?.takeIf { it.isNotBlank() }?.let { return it }

        val email = System.getenv("VACAPP_EMAIL") ?: return null
        val password = System.getenv("VACAPP_PASSWORD") ?: return null
        val auth = retrofit(null).create(AuthService::class.java)
        val response = runBlocking { auth.login(LoginRequest(email, password)) }
        return if (response.isSuccessful) response.body()?.token else null
    }

    @Test
    fun `general chat without a token is rejected`() = runBlocking {
        assumeIntegrationEnabled()

        val response = aiService(token = null).sendGeneralChat(GeneralChatRequest("Hello"))

        assertTrue(
            "Expected 401/403 for an unauthenticated AI call but got ${response.code()}",
            response.code() == 401 || response.code() == 403
        )
    }

    @Test
    fun `general chat round-trips for a Plus user`() = runBlocking {
        assumeIntegrationEnabled()
        val token = resolveToken()
        assumeTrue("No VACAPP_TOKEN or VACAPP_EMAIL/PASSWORD provided.", token != null)

        val response = aiService(token).sendGeneralChat(
            GeneralChatRequest("How many bovines do I have registered?")
        )

        // A Free account is gated with 403 -> skip instead of fail.
        assumeTrue("Token is not on the Plus plan (403).", response.code() != 403)

        assertTrue("AI call failed: HTTP ${response.code()}", response.isSuccessful)
        assertTrue("Empty AI response", response.body()?.response?.isNotBlank() == true)
    }

    @Test
    fun `bovine chat round-trips for a Plus user`() = runBlocking {
        assumeIntegrationEnabled()
        val token = resolveToken()
        assumeTrue("No token provided.", token != null)
        val bovineId = System.getenv("VACAPP_BOVINE_ID")?.toIntOrNull()
        assumeTrue("No VACAPP_BOVINE_ID provided.", bovineId != null)

        val response = aiService(token).sendBovineChat(
            BovineChatRequest(bovineId!!, "Summarize this bovine's recent health.")
        )

        assumeTrue("Token is not on the Plus plan (403).", response.code() != 403)

        assertTrue("AI call failed: HTTP ${response.code()}", response.isSuccessful)
        assertTrue("Empty AI response", response.body()?.response?.isNotBlank() == true)
    }

    @Test
    fun `bovine chat history is retrievable for a Plus user`() = runBlocking {
        assumeIntegrationEnabled()
        val token = resolveToken()
        assumeTrue("No token provided.", token != null)
        val bovineId = System.getenv("VACAPP_BOVINE_ID")?.toIntOrNull()
        assumeTrue("No VACAPP_BOVINE_ID provided.", bovineId != null)

        val response = aiService(token).getBovineChatHistory(bovineId!!)

        assumeTrue("Token is not on the Plus plan (403).", response.code() != 403)
        assertTrue("History call failed: HTTP ${response.code()}", response.isSuccessful)
    }
}
