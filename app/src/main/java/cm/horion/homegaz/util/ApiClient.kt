package cm.horion.homegaz.util

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object ApiClient {

    val client = HttpClient(CIO) {

        install(HttpTimeout) {
            requestTimeoutMillis = 60_000 // 60 secondes
            connectTimeoutMillis = 15_000
            socketTimeoutMillis = 60_000
        }

        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
            })
        }
//        defaultRequest {
//            header("X-Client-Type", "MOBILE")
//        }
    }


}