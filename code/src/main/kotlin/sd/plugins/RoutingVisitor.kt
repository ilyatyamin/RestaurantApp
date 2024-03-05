package sd.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
private data class MakeOrderData(val token: String, val listOfOrder: MutableMap<Int, Int>)

@Serializable
private data class AddToOrderData(val token: String, val orderId: Int, val dishId: Int)

@Serializable
private data class AddToOrderManyDishData(val token: String, val orderId: Int, val dishes: MutableList<Int>)

@Serializable
private data class JustOrderData(val token: String, val orderId: Int)

@Serializable
private data class FeedbackOrderData(val token: String, val orderId: Int, val stars: Int, val comment: String)

@Serializable
private data class JustTokenData(val token: String)

fun Application.configureRoutingVisitorSystem() {
    routing {
        post("/makeOrder") {
            try {
                val rawData = call.receive<String>()
                val data = Json.decodeFromString<MakeOrderData>(rawData)
                val visitor = TokenSystem.getVisitorByToken(data.token)
                val id = visitor.makeOrder(data.listOfOrder)
                call.respond(HttpStatusCode.OK, id.toString())
            } catch (ex: BadRequestException) {
                call.respond(HttpStatusCode.BadRequest,"Data of request is incorrect")
            } catch (ex: Exception) {
                call.respond(HttpStatusCode.BadRequest, ex.message.toString())
            }
        }

        post("/addToOrderOneDish") {
            try {
                val rawData = call.receive<String>()
                val data = Json.decodeFromString<AddToOrderData>(rawData)
                val visitor = TokenSystem.getVisitorByToken(data.token)
                val id = visitor.addToOrder(data.orderId, data.dishId)
                call.respond(HttpStatusCode.OK, id.toString())
            } catch (ex: BadRequestException) {
                call.respond(HttpStatusCode.BadRequest,"Data of request is incorrect")
            } catch (ex: Exception) {
                call.respond(HttpStatusCode.BadRequest, ex.message.toString())
            }
        }

        post("/addToOrderManyDish") {
            try {
                val rawData = call.receive<String>()
                val data = Json.decodeFromString<AddToOrderManyDishData>(rawData)
                val visitor = TokenSystem.getVisitorByToken(data.token)
                val id = visitor.addToOrder(data.orderId, data.dishes)
                call.respond(HttpStatusCode.OK, id.toString())
            } catch (ex: BadRequestException) {
                call.respond(HttpStatusCode.BadRequest,"Data of request is incorrect")
            } catch (ex: Exception) {
                call.respond(HttpStatusCode.BadRequest, ex.message.toString())
            }
        }

        post("/cancelOrder") {
            try {
                val rawData = call.receive<String>()
                val data = Json.decodeFromString<JustOrderData>(rawData)
                val visitor = TokenSystem.getVisitorByToken(data.token)
                val id = visitor.cancelOrder(data.orderId)
                call.respond(HttpStatusCode.OK, id.toString())
            } catch (ex: BadRequestException) {
                call.respond(HttpStatusCode.BadRequest,"Data of request is incorrect")
            } catch (ex: Exception) {
                call.respond(HttpStatusCode.BadRequest, ex.message.toString())
            }
        }

        get("/getOrderStatus") {
            try {
                val rawData = call.receive<String>()
                val data = Json.decodeFromString<JustOrderData>(rawData)
                val visitor = TokenSystem.getVisitorByToken(data.token)
                call.respond(HttpStatusCode.OK, visitor.getOrderStatus(data.orderId).toString())
            } catch (ex: BadRequestException) {
                call.respond(HttpStatusCode.BadRequest,"Data of request is incorrect")
            } catch (ex: Exception) {
                call.respond(HttpStatusCode.BadRequest, ex.message.toString())
            }
        }

        post("/payOrder") {
            try {
                val rawData = call.receive<String>()
                val data = Json.decodeFromString<JustOrderData>(rawData)
                val visitor = TokenSystem.getVisitorByToken(data.token)
                visitor.payOrder(data.orderId)
                call.respond(HttpStatusCode.OK, "OK")
            } catch (ex: BadRequestException) {
                call.respond(HttpStatusCode.BadRequest,"Data of request is incorrect")
            } catch (ex: Exception) {
                call.respond(HttpStatusCode.BadRequest, ex.message.toString())
            }
        }

        post("/leaveFeedbackAboutData") {
            try {
                val rawData = call.receive<String>()
                val data = Json.decodeFromString<FeedbackOrderData>(rawData)
                val visitor = TokenSystem.getVisitorByToken(data.token)
                visitor.leaveFeedbackAboutOrder(data.orderId, data.stars, data.comment)
                call.respond(HttpStatusCode.OK, "OK")
            } catch (ex: BadRequestException) {
                call.respond(HttpStatusCode.BadRequest,"Data of request is incorrect")
            } catch (ex: Exception) {
                call.respond(HttpStatusCode.BadRequest, ex.message.toString())
            }
        }

        get("/getCurrentDishesInWeb") {
            try {
                val rawData = call.receive<String>()
                val data = Json.decodeFromString<JustTokenData>(rawData)
                val visitor = TokenSystem.getVisitorByToken(data.token)
                call.respond(HttpStatusCode.OK, visitor.getCurrentDishes().toString())
            } catch (ex: BadRequestException) {
                call.respond(HttpStatusCode.BadRequest,"Data of request is incorrect")
            } catch (ex: Exception) {
                call.respond(HttpStatusCode.BadRequest, ex.message.toString())
            }
        }
    }
}
