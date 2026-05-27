package com.fatec.at2_base.network

import com.fatec.at2_base.BASE_URL
import com.fatec.at2_base.model.Valorant
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object ApiClient {

    private val httpClient = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                prettyPrint = false
            })
        }
    }

    suspend fun getAgents(): List<Valorant> {
        return httpClient.get("$BASE_URL/valorant").body()
    }

    suspend fun getAgentById(id: Int): Valorant {
        return httpClient.get("$BASE_URL/valorant/$id").body()
    }

    suspend fun createAgent(
        agent: String,
        description: String,
        country: String
    ): Valorant {
        return createAgent(
            Valorant(
                id = 0,
                agent = agent,
                description = description,
                country = country
            )
        )
    }

    suspend fun createAgent(valorant: Valorant): Valorant {
        return httpClient.post("$BASE_URL/valorant") {
            contentType(ContentType.Application.Json)
            setBody(valorant)
        }.body()
    }

    suspend fun updateAgent(
        id: Int,
        agent: String,
        description: String,
        country: String
    ): Valorant {
        return updateAgent(
            id = id,
            valorant = Valorant(
                id = id,
                agent = agent,
                description = description,
                country = country
            )
        )
    }

    suspend fun updateAgent(id: Int, valorant: Valorant): Valorant {
        return httpClient.put("$BASE_URL/valorant/$id") {
            contentType(ContentType.Application.Json)
            setBody(valorant)
        }.body()
    }

    suspend fun deleteAgent(id: Int) {
        httpClient.delete("$BASE_URL/valorant/$id")
    }
}
