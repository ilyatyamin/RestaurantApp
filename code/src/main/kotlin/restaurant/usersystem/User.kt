package restaurant.usersystem

import restaurant.order.OrderSystem

open class User(val id : Int, private val login: String, private val password: String,
                private var role: UserRole, protected val orderSystem: OrderSystem) {
    fun compareData(log: String, passw: String): Boolean {
        return login == log && password == passw
    }

    var isLoggedNow: Boolean = false
}