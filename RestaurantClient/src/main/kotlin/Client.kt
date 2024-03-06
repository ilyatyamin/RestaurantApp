import DishParams.DishParams
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

internal class Client {
    private val client = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_2)
        .connectTimeout(Duration.ofSeconds(30)).build()
    private var token: String = ""

    init {
        val result = makeRequest("", TypeOfRequest.GET, "")
        if (result?.statusCode() != 200) {
            println("WARNING!!! Сервер не запущен. Status  = ${result?.statusCode()}")
        }
    }

    enum class TypeOfRequest {
        GET, POST
    }

    fun loginUser(login: String, password: String): String {
        val result = makeRequest("loginVisitor", TypeOfRequest.POST, Json.encodeToString(LoginData(login, password)))
        return if (result?.statusCode() == 200) {
            token = result.body()
            "OK"
        } else {
            result?.body() ?: "null"
        }
    }

    fun loginAdmin(login: String, password: String): String {
        val result = makeRequest("loginAdmin", TypeOfRequest.POST, Json.encodeToString(LoginData(login, password)))
        return if (result?.statusCode() == 200) {
            token = result.body()
            "OK"
        } else {
            result?.body() ?: "null"
        }
    }

    fun registerNewAdmin(login: String, password: String): String {
        val result =
            makeRequest("registerNewAdmin", TypeOfRequest.POST, Json.encodeToString(LoginData(login, password)))
        return if (result?.statusCode() == 200) {
            "OK"
        } else {
            result?.body() ?: "null"
        }
    }

    fun registerNewVisitor(login: String, password: String): String {
        val result =
            makeRequest("registerNewVisitor", TypeOfRequest.POST, Json.encodeToString(LoginData(login, password)))
        return if (result?.statusCode() == 200) {
            "OK"
        } else {
            result?.body() ?: "null"
        }
    }

    // Admin block
    fun addDishToMenu(name: String, price: Int, timeProduction: String, count: Int = 1): String {
        val result =
            makeRequest(
                "admin/addDishToMenu",
                TypeOfRequest.POST,
                Json.encodeToString(AddDishData(token, name, price, timeProduction, count))
            )
        return result?.body() ?: "null"
    }

    fun removeDishFromMenu(id: Int): String {
        val result =
            makeRequest(
                "admin/removeDishFromMenu",
                TypeOfRequest.POST,
                Json.encodeToString(RemoveDishData(token, id.toString()))
            )
        return result?.body() ?: "null"
    }

    fun setParamToDish(dishId: Int, params: DishParams, type: String): String {
        val result =
            makeRequest(
                "admin/setParamToDish",
                TypeOfRequest.POST,
                Json.encodeToString(SetParamData(token, dishId.toString(), params.toString(), type))
            )
        return result?.body() ?: "null"
    }

    fun increaseTheNumberOfDish(dishId: Int, delta: Int): String {
        val result =
            makeRequest(
                "admin/increaseTheNumberOfDish",
                TypeOfRequest.POST,
                Json.encodeToString(AddNumberToDishData(token, dishId.toString(), delta))
            )
        return result?.body() ?: "null"
    }

    fun getStatistics(): String {
        val result =
            makeRequest("admin/getStatistics", TypeOfRequest.POST, Json.encodeToString(JustToken(token)))
        return result?.body() ?: "null"
    }

    fun getMenu(): String {
        val result =
            makeRequest("getMenu", TypeOfRequest.POST, "")
        return result?.body() ?: "null"
    }

    fun exitAdmin(): String {
        val result =
            makeRequest("admin/exit", TypeOfRequest.POST, "")
        return result?.body() ?: "OK"
    }

    // Visitor block
    fun makeOrder(listOfOrder: MutableMap<Int, Int>): String {
        val result =
            makeRequest("order/makeOrder", TypeOfRequest.POST, Json.encodeToString(MakeOrderData(token, listOfOrder)))
        return result?.body() ?: "null"
    }

    fun addToOrder(orderId: Int, dishId: Int): String {
        val result =
            makeRequest(
                "order/addToOrderOneDish",
                TypeOfRequest.POST,
                Json.encodeToString(AddToOrderData(token, orderId, dishId))
            )
        return result?.body() ?: "null"
    }

    fun addToOrder(orderId: Int, dishId: MutableMap<Int, Int>): String {
        val result =
            makeRequest(
                "order/addToOrderManyDish",
                TypeOfRequest.POST,
                Json.encodeToString(AddToOrderManyDishData(token, orderId, dishId))
            )
        if (result?.statusCode() == 200) {
            return "OK"
        }
        return result?.body() ?: "null"
    }

    fun cancelOrder(orderId: Int): String {
        val result =
            makeRequest("order/cancelOrder", TypeOfRequest.POST, Json.encodeToString(JustOrderData(token, orderId)))
        if (result?.statusCode() == 200) {
            return "OK"
        } else {
            return result?.body() ?: "null"
        }
    }

    fun getOrderStatus(orderId: Int): String {
        val result =
            makeRequest("order/getOrderStatus", TypeOfRequest.POST, Json.encodeToString(JustOrderData(token, orderId)))
        return result?.body() ?: "null"
    }

    fun payOrder(orderId: Int): String {
        val result =
            makeRequest("order/payOrder", TypeOfRequest.POST, Json.encodeToString(JustOrderData(token, orderId)))
        return result?.body() ?: "null"
    }

    fun leaveFeedbackAboutOrder(orderId: Int, stars: Int, comment: String): String {
        val result =
            makeRequest(
                "order/leaveFeedbackAboutData",
                TypeOfRequest.POST,
                Json.encodeToString(FeedbackOrderData(token, orderId, stars, comment))
            )
        return result?.body() ?: "null"
    }

    fun exitVisitor(): String {
        val result =
            makeRequest("visitor/exit", TypeOfRequest.POST, "")
        return result?.body() ?: "OK"
    }


    private fun makeRequest(pathToRequest: String, type: TypeOfRequest, jsonStr: String): HttpResponse<String>? {
        return try {
            val request: HttpRequest = if (type == TypeOfRequest.GET) {
                HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/$pathToRequest"))
                    .GET().build()
            } else {
                HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/$pathToRequest"))
                    .POST(HttpRequest.BodyPublishers.ofString(jsonStr)).build()
            }
            val result = client.send(request, HttpResponse.BodyHandlers.ofString())
            result
        } catch (e: Exception) {
            null
        }
    }
}

@Serializable
private data class LoginData(val login: String, val password: String)

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
private data class SetParamData(val token: String, val dishId: String, val params: String, val type: String)

@Serializable
private data class AddNumberToDishData(val token: String, val dishId: String, val delta: Int)

@Serializable
private data class MakeOrderData(val token: String, val listOfOrder: MutableMap<Int, Int>)

@Serializable
private data class AddToOrderData(val token: String, val orderId: Int, val dishId: Int)

@Serializable
private data class AddToOrderManyDishData(val token: String, val orderId: Int, val dishes: MutableMap<Int, Int>)

@Serializable
private data class JustOrderData(val token: String, val orderId: Int)

@Serializable
private data class FeedbackOrderData(val token: String, val orderId: Int, val stars: Int, val comment: String)

@Serializable
private data class JustTokenData(val token: String)