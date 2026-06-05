package pe.edu.upc.vacapp.shared.data.di

import androidx.room.Room
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import okhttp3.OkHttpClient
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import pe.edu.upc.vacapp.Vacapp
import pe.edu.upc.vacapp.shared.data.local.AppDatabase
import pe.edu.upc.vacapp.shared.data.remote.ApiConstants
import pe.edu.upc.vacapp.shared.data.remote.AuthInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.util.Locale

object SharedDataModule {
    // Singleton instance of Retrofit and AppDatabase
    private var dbInstance: AppDatabase? = null
    private var retrofitInstance: Retrofit? = null

    fun getRetrofit(): Retrofit {
        if (retrofitInstance == null) {
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(AuthInterceptor())
                .build()

            val customGson = GsonBuilder()
                .registerTypeAdapter(LocalDate::class.java, LocalDateAdapter())
                .create()

            retrofitInstance = Retrofit.Builder()
                .baseUrl(ApiConstants.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(customGson))
                .build()
        }
        return retrofitInstance!!
    }

    fun getAppDatabase(): AppDatabase {
        if (dbInstance == null) {
            dbInstance = Room.databaseBuilder(
                Vacapp.instance.applicationContext,
                AppDatabase::class.java,
                "vacapp-db"
            ).build()
        }
        return dbInstance!!
    }
}


class LocalDateAdapter : JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
    override fun serialize(
        src: LocalDate,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement {
        return JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE))
    }

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): LocalDate {
        return try {
            LocalDate.parse(json.asString, DateTimeFormatter.ISO_LOCAL_DATE)
        } catch (e: Exception) {
            throw JsonParseException("Error al parsear la fecha: ${json.asString}", e)
        }
    }
}

val localePeru = Locale("es", "PE")
val timeFormatter = DateTimeFormatter.ofPattern("dd-MMM", localePeru)