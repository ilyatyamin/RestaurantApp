package sd

import sd.plugins.configureRoutingVisitorSystem
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.util.*
import restaurant.System
import sd.plugins.*

//val getter = AttributeKey<System>("systemGetter")

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
//    attributes.put(getter, System())

    configureSecurity()
    configureSerialization()
    configureRoutingAuthSystem()
    configureRoutingAdminSystem()
    configureRoutingVisitorSystem()
    configureRoutingMenu()
}
