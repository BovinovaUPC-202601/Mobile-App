package pe.edu.upc.vacapp.collars.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pe.edu.upc.vacapp.collars.data.model.ReassignCollarRequest
import pe.edu.upc.vacapp.collars.data.model.RegisterCollarRequest
import pe.edu.upc.vacapp.collars.data.remote.CollarService
import pe.edu.upc.vacapp.collars.domain.model.Collar
import pe.edu.upc.vacapp.collars.domain.model.CollarCapacity

/** Raised when the backend gates collars behind the Plus plan (403). */
class CollarsRequirePlusException(
    message: String = "Los collares IoT están disponibles en el plan Plus."
) : Exception(message)

class CollarRepository(
    private val service: CollarService
) {
    suspend fun getCollars(): List<Collar> = withContext(Dispatchers.IO) {
        val response = service.getCollars()
        if (response.isSuccessful) response.body()?.map { it.toDomain() } ?: emptyList()
        else emptyList()
    }

    /** Throws [CollarsRequirePlusException] on 403 so the UI can show the Plus hint. */
    suspend fun getCapacity(): CollarCapacity = withContext(Dispatchers.IO) {
        val response = service.getCapacity()
        when {
            response.isSuccessful -> response.body()?.toDomain() ?: CollarCapacity()
            response.code() == 403 -> throw CollarsRequirePlusException()
            else -> CollarCapacity()
        }
    }

    suspend fun register(deviceId: String, bovineId: Int): Boolean = withContext(Dispatchers.IO) {
        service.register(RegisterCollarRequest(deviceId, bovineId)).isSuccessful
    }

    suspend fun reassign(collarId: Int, bovineId: Int): Boolean = withContext(Dispatchers.IO) {
        service.reassign(collarId, ReassignCollarRequest(bovineId)).isSuccessful
    }

    suspend fun remove(collarId: Int): Boolean = withContext(Dispatchers.IO) {
        service.remove(collarId).isSuccessful
    }
}
