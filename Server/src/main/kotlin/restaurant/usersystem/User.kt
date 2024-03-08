package restaurant.usersystem

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import restaurant.order.OrderSystem

@Serializable
sealed class User {

    /**
     * Compare data with user's data
     * @return True if inputted data from this account, False vice versa
     */
    abstract fun compareData(log: String, passw: String): Boolean

    @Serializable
    var isLoggedNow: Boolean = false

    @Serializable
    var role = UserRole.Visitor

    @Transient
    protected lateinit var orderSystem: OrderSystem

    @JvmName("set role")
    protected fun setRole(newRole: UserRole) {
        role = newRole
    }

    internal fun setOS(order: OrderSystem): User {
        orderSystem = order
        return this
    }

}