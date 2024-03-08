package restaurant.usersystem

import kotlinx.serialization.Serializable
import restaurant.order.ImportanceLevel
import restaurant.order.OrderStatus

@Serializable
class Visitor(
    var id: Int, private var login: String, private var password: String
) : User() {
    init {
        setRole(UserRole.Visitor)
    }

    @Serializable private var counterOrders = 0
    @Serializable private var visitorStatus = UserStatus.Beginner

    override fun compareData(log: String, passw: String): Boolean {
        return login == log && password == passw
    }

    /**
     * Match user status with order's priority level
     * @return ImportanceLevel of order
     */
    private fun matchStatusWithLevel(): ImportanceLevel {
        return when (visitorStatus) {
            UserStatus.Beginner -> {
                ImportanceLevel.Low
            }

            UserStatus.Medium -> {
                ImportanceLevel.Medium
            }

            UserStatus.Lover -> {
                ImportanceLevel.High
            }
        }
    }

    /**
     * Creates order for this user
     * @param mapOfOrder: map where key is dishId and value is the amount of dish's portions.
     * @return id of order in system
     */
    @JvmName("MakeOrderByInt")
    fun makeOrder(mapOfOrder: MutableMap<Int, Int>): Int {
        if (isLoggedNow) {
            return orderSystem.addOrder(mapOfOrder, matchStatusWithLevel(), id)
        } else {
            throw SecurityException("You are not logged now")
        }
    }

    /**
     * Add one dish to existed order.
     *
     * @param orderId id of order, where system need to add the dish
     * @param dishId id of dish, which should be added to the order
     */
    fun addToOrder(orderId: Int, dishId: Int) {
        if (isLoggedNow) {
            orderSystem.addToExistedOrder(orderId, dishId, id)
        } else {
            throw SecurityException("You are not logged now")
        }
    }

    /**
     * Add many dishes to existed order.
     *
     * @param orderId id of order, where system need to add the dishes
     * @param dishes map of dishes, where key is dishId and value is amount
     */
    fun addToOrder(orderId: Int, dishes: MutableMap<Int, Int>) {
        if (isLoggedNow) {
            orderSystem.addToExistedOrder(orderId, dishes, id)
        } else {
            throw SecurityException("You are not logged now")
        }
    }

    /**
     * Cancel the existed order
     *
     * @param orderId: id of order that should be cancelled
     */
    fun cancelOrder(orderId: Int)  {
        if (isLoggedNow) {
            orderSystem.cancelOrder(orderId, id)
        } else {
            throw SecurityException("You are not logged now")
        }
    }

    /**
     * Get existed order status
     *
     * @param orderId: id of order
     * @return OrderStatus object
     */
    fun getOrderStatus(orderId: Int): OrderStatus {
        if (isLoggedNow) {
            return orderSystem.getOrderStatus(orderId, id)
        } else {
            throw SecurityException("You are not logged now")
        }
    }

    /**
     * Pay existed order
     *
     * @param orderId: id of order
     * @return current bill for the order
     */
    fun payOrder(orderId: Int) : Int {
        if (isLoggedNow) {
            increaseLevel()
            return orderSystem.payOrder(orderId, id)
        } else {
            throw SecurityException("You are not logged now")
        }
    }

    /**
     * Increase the customer's level in dependent of the total order's number
     */
    private fun increaseLevel() {
        ++counterOrders
        if (counterOrders in 11..29) {
            visitorStatus = UserStatus.Medium
        } else if (counterOrders > 30) {
            visitorStatus = UserStatus.Lover
        }
    }

    /**
     * Leave feedback about user's order.
     *
     * @param orderId: id of order, that should be starred
     * @param stars the number of stars that user leaved for the order
     * @param comment the user's comment
     */
    fun leaveFeedbackAboutOrder(orderId: Int, stars: Int, comment: String) {
        if (isLoggedNow) {
            orderSystem.setReviewToOrder(orderId, stars, comment, id)
        } else {
            throw SecurityException("You are not logged now")
        }
    }

}