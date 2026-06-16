package pe.edu.upc.vacapp.shared.data.remote

import com.google.gson.JsonParser
import retrofit2.Response

fun <T> Response<T>.errorMessage(): String {
    val errorBody = errorBody()?.string() ?: return "Error desconocido"
    return try {
        val json = JsonParser.parseString(errorBody)
        if (json.isJsonObject) {
            val obj = json.asJsonObject
            obj.get("message")?.takeIf { it.isJsonPrimitive }?.asString?.takeIf { it.isNotBlank() }
                ?: obj.get("title")?.takeIf { it.isJsonPrimitive }?.asString?.takeIf { it.isNotBlank() }
                ?: obj.getAsJsonObject("errors")?.entrySet()?.firstOrNull()?.value?.asJsonArray?.firstOrNull()?.takeIf { it.isJsonPrimitive }?.asString
                ?: errorBody
        } else if (json.isJsonPrimitive) {
            json.asString
        } else {
            errorBody
        }
    } catch (e: Exception) {
        errorBody
    }
}
