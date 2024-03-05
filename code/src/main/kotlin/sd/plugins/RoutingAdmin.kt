package sd.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import restaurant.dish.DishParams
import kotlin.time.Duration

@Serializable
private data class JustToken(val token: String)

@Serializable
private data class AddDishData(
    val token: String,
    val name: String,
    val price: Int,
    val timeProduction: String,
    val count: Int = 1
)

@Serializable
private data class RemoveDishData(val token: String, val id: String)

@Serializable
private data class SetParamData(val token: String, val dishId: Int, val params: DishParams, @Contextual val type: String)

@Serializable
private data class AddNumberToDishData(val token: String, val dishId: String, val delta: Int)

fun Application.configureRoutingAdminSystem() {
    routing {
        post("/addDishToMenu") {
            try {
                val rawData = call.receive<String>()
                val data = Json.decodeFromString<AddDishData>(rawData)
                val admin = TokenSystem.getAdminByToken(data.token)
                val id = admin.addDishToMenu(data.name, data.price, Duration.parse(data.timeProduction), data.count)
                call.respond(HttpStatusCode.OK, id.toString())
            } catch (ex: BadRequestException) {
                call.respond(HttpStatusCode.BadRequest,"Data of request is incorrect")
            } catch (ex: Exception) {
                call.respond(HttpStatusCode.BadRequest, ex.message.toString())
            }
        }

        post("/removeDishFromMenu") {
            try {
                val rawData = call.receive<String>()
                val data = Json.decodeFromString<RemoveDishData>(rawData)
                val admin = TokenSystem.getAdminByToken(data.token)
                admin.removeDishFromMenu(data.id.toInt())
                call.respond(HttpStatusCode.OK, "OK")
            } catch (ex: BadRequestException) {
                call.respond(HttpStatusCode.BadRequest,"Data of request is incorrect")
            } catch (ex: Exception) {
                call.respond(HttpStatusCode.BadRequest, ex.message.toString())
            }
        }

        post("/setParamToDish") {
            try {
                val rawData = call.receive<String>()
                val data = Json.decodeFromString<SetParamData>(rawData)
                val admin = TokenSystem.getAdminByToken(data.token)
                admin.setParamToDish(data.dishId, data.params, data.type)
                call.respond(HttpStatusCode.OK, "OK")
            } catch (ex: BadRequestException) {
                call.respond(HttpStatusCode.BadRequest,"Data of request is incorrect")
            } catch (ex: Exception) {
                call.respond(HttpStatusCode.BadRequest, ex.message.toString())
            }
        }

        post("/increaseTheNumberOfDish") {
            try {
                val rawData = call.receive<String>()
                val data = Json.decodeFromString<AddNumberToDishData>(rawData)
                val admin = TokenSystem.getAdminByToken(data.token)
                admin.increaseTheNumberOfDish(data.dishId.toInt(), data.delta)
                call.respond(HttpStatusCode.OK, "OK")
            } catch (ex: BadRequestException) {
                call.respond(HttpStatusCode.BadRequest,"Data of request is incorrect")
            } catch (ex: Exception) {
                call.respond(HttpStatusCode.BadRequest, ex.message.toString())
            }
        }

        get("/getStatistics") {
            try {
                val rawData = call.receive<String>()
                val data = Json.decodeFromString<JustToken>(rawData)
                val admin = TokenSystem.getAdminByToken(data.token)
                call.respond(HttpStatusCode.OK, admin.getStatistics())
            } catch (ex: BadRequestException) {
                call.respond(HttpStatusCode.BadRequest,"Data of request is incorrect")
            } catch (ex: Exception) {
                call.respond(HttpStatusCode.BadRequest, ex.message.toString())
            }
        }

        get("/getCurrentDishesForAdmin") {
            try {
                val rawData = call.receive<String>()
                val data = Json.decodeFromString<JustToken>(rawData)
                val admin = TokenSystem.getAdminByToken(data.token)
                call.respond(HttpStatusCode.OK, admin.getCurrentDishes().toString())
            } catch (ex: BadRequestException) {
                call.respond(HttpStatusCode.BadRequest,"Data of request is incorrect")
            } catch (ex: Exception) {
                call.respond(HttpStatusCode.BadRequest, ex.message.toString())
            }
        }
    }
}
