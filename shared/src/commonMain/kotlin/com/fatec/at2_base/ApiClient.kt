package com.fatec.at2_base

import com.fatec.at2_base.model.Valorant
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

expect fun createHttpClient(): HttpClient

object ApiClient {

    private val client = createHttpClient()

    suspend fun getAgents(): List<Valorant> {
        return client.get("$BASE_URL/valorant").body()
    }

    suspend fun getAgentById(id: Int): Valorant {
        return client.get("$BASE_URL/valorant/$id").body()
    }

    suspend fun createAgent(agent: String, description: String, country: String): Valorant {
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
        return client.post("$BASE_URL/valorant") {
            contentType(ContentType.Application.Json)
            setBody(valorant)
        }.body()
    }

    suspend fun updateAgent(id: Int, valorant: Valorant): Valorant {
        return client.put("$BASE_URL/valorant/$id") {
            contentType(ContentType.Application.Json)
            setBody(valorant)
        }.body()
    }

    suspend fun deleteAgent(id: Int) {
        client.delete("$BASE_URL/valorant/$id")
    }
}
