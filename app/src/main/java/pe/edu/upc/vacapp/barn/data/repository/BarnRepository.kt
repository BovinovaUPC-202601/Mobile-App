package pe.edu.upc.vacapp.barn.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pe.edu.upc.vacapp.barn.data.model.CreateBarnRequest
import pe.edu.upc.vacapp.barn.data.remote.BarnService
import pe.edu.upc.vacapp.barn.domain.model.Barn
import pe.edu.upc.vacapp.shared.data.remote.errorMessage

class BarnRepository(
    private val barnService: BarnService
) {
    suspend fun addBarn(
        barn: Barn
    ) = withContext(Dispatchers.IO) {
        val data = CreateBarnRequest.fromBarn(barn)
        val response = barnService.createBarn(data)
        if (response.isSuccessful) {
            response.body()
        } else {
            throw Exception(response.errorMessage())
        }
    }

    suspend fun getBarns(): List<Barn> = withContext(Dispatchers.IO) {
        val response = barnService.getBarns()

        if (response.isSuccessful) {
            return@withContext response.body()?.map {
                it.toBarn()
            } ?: emptyList()
        }

        throw Exception(response.errorMessage())
    }
}