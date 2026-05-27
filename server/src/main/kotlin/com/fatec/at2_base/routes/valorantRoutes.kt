package com.fatec.at2_base.routes

import com.fatec.at2_base.db.Valorant
import com.fatec.at2_base.db.ValorantDatabase
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.valorantRoutes() {
    route("/valorant") {
        get {
            call.respond(ValorantDatabase.agents)
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toLongOrNull()
            val agent = ValorantDatabase.agents.firstOrNull { it.id == id }

            if (agent == null) {
                call.respond(HttpStatusCode.NotFound, "Agente nao encontrado")
                return@get
            }

            call.respond(agent)
        }

        post {
            val request = call.receive<Valorant>()
            val nextId = (ValorantDatabase.agents.maxOfOrNull { it.id } ?: 0L) + 1
            val agent = request.copy(id = nextId)

            ValorantDatabase.agents.add(agent)
            call.respond(HttpStatusCode.Created, agent)
        }

        put("/{id}") {
            val id = call.parameters["id"]?.toLongOrNull()
            val index = ValorantDatabase.agents.indexOfFirst { it.id == id }

            if (index == -1 || id == null) {
                call.respond(HttpStatusCode.NotFound, "Agente nao encontrado")
                return@put
            }

            val agent = call.receive<Valorant>().copy(id = id)
            ValorantDatabase.agents[index] = agent

            call.respond(agent)
        }

        delete("/{id}") {
            val id = call.parameters["id"]?.toLongOrNull()
            val removed = ValorantDatabase.agents.removeIf { it.id == id }

            if (!removed) {
                call.respond(HttpStatusCode.NotFound, "Agente nao encontrado")
                return@delete
            }

            call.respond(HttpStatusCode.NoContent)
        }
    }
}

