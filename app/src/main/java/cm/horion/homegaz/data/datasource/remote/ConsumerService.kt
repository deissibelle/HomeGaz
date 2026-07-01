package cm.horion.homegaz.data.datasource.remote

import android.util.Log
import cm.horion.homegaz.domain.model.Endpoint
import cm.horion.homegaz.domain.model.consommateur.dto.GazBottle
import cm.horion.homegaz.domain.model.consommateur.dto.Profile
import cm.horion.homegaz.domain.model.consommateur.request.ProfileRequest
import cm.horion.homegaz.domain.model.distributor.dto.Distributor
import cm.horion.homegaz.domain.model.response.Response
import cm.horion.homegaz.domain.usecase.LoadGazProfileUseCase
import cm.horion.homegaz.util.ApiClient.client
import cm.horion.homegaz.util.Constants.GAZ_URL
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.serialization.json.Json

class ConsumerService(
    private val loadProfile : LoadGazProfileUseCase
) {

    suspend fun getGaz() : List<GazBottle> {
        val response: HttpResponse = client.get("$GAZ_URL${Endpoint.GetGaz.path}") {
            accept(ContentType.Application.Json)
        }
        if (response.status == HttpStatusCode.OK) {
            val responseText = response.bodyAsText()
            return Json.decodeFromString<List<GazBottle>>(responseText)
        } else {
            val responseText = response.bodyAsText()
            return emptyList()
        }
    }

    suspend fun saveProfil(request : ProfileRequest) : Response {
        return try {
            val response: HttpResponse = client.post("$GAZ_URL${Endpoint.SaveProfile.path}") {
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
                setBody(request)
            }
            val responseText = response.bodyAsText()
            Log.d("PROFILE",responseText)
            val profil = Json.decodeFromString<Response>(responseText)
            if (profil.profile != null) {
                loadProfile.save(profil.profile)
            }
            profil
        } catch (e : Exception){
            Response(false, e.message.toString())
        }

    }

    suspend fun updateProfile(request : ProfileRequest) : Response {
        val response: HttpResponse = client.put("$GAZ_URL${Endpoint.UpdateProfile.path}") {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            setBody(request)
        }
        val responseText = response.bodyAsText()
        val profil = Json.decodeFromString<Response>(responseText)
        if (profil.profile != null){
            loadProfile.save(profil.profile)
        }
        return profil
    }

    suspend fun getProfile() : Response {
        val response: HttpResponse = client.get("$GAZ_URL${Endpoint.GetProfile.path}") {
            accept(ContentType.Application.Json)
        }
        Log.d("PROFILE",response.bodyAsText())
        if (response.status == HttpStatusCode.OK) {
            val responseText = response.bodyAsText()
            val profile = Json.decodeFromString<Profile>(responseText)
            loadProfile.save(profile)
            return Response(true,"profile trouver",profile)
        } else {
            val responseText = response.bodyAsText()
            return Json.decodeFromString<Response>(responseText)
        }
    }

    suspend fun getDepotGaz(latitude : Double, longitude: Double,radiusKm: String,battleUuid: String) : List<Distributor> {
        val response: HttpResponse = client.get("$GAZ_URL${Endpoint.GetDepotGaz.path}") {
            accept(ContentType.Application.Json)
            url {
                parameters.append("latitude", latitude.toString())
                parameters.append("longitude", longitude.toString())
                parameters.append("radiusKm", radiusKm)
                parameters.append("battleUuid", battleUuid)

            }
        }

        if (response.status == HttpStatusCode.OK) {
            val responseText = response.bodyAsText()
            return Json.decodeFromString<List<Distributor>>(responseText)
        } else {
            val errorText = response.bodyAsText()
            throw Exception("Erreur serveur (${response.status}): $errorText")
        }
    }



}