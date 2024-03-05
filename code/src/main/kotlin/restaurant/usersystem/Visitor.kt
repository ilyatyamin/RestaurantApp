package restaurant.usersystem

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import restaurant.order.ImportanceLevel
import restaurant.order.OrderStatus
import restaurant.order.OrderSystem

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

    @JvmName("MakeOrderByInt")
    fun makeOrder(listOfOrder: MutableMap<Int, Int>): Int {
        if (isLoggedNow) {
            return orderSystem.addOrder(listOfOrder, matchStatusWithLevel(), id)
        } else {
            throw SecurityException("You are not logged now")
        }
    }

    @JvmName("MakeOrderByString")
    fun makeOrder(listOfOrder: MutableMap<String, Int>): Int {
        if (isLoggedNow) {
            return orderSystem.addOrder(listOfOrder, matchStatusWithLevel(), id)
        } else {
            throw SecurityException("You are not logged now")
        }
    }

    fun getCurrentDishes() : List<Int> {
        return orderSystem.menuObj.getCurrentDishesId
    }

    fun addToOrder(orderId: Int, dishId: Int) {
        if (isLoggedNow) {
            orderSystem.addToExistedOrder(orderId, dishId)
        } else {
            throw SecurityException("You are not logged now")
        }
    }

    fun addToOrder(orderId: Int, dishes: MutableList<Int>) {
        if (isLoggedNow) {
            orderSystem.addToExistedOrder(orderId, dishes)
        } else {
            throw SecurityException("You are not logged now")
        }
    }

    fun cancelOrder(orderId: Int) {
        if (isLoggedNow) {
            orderSystem.cancelOrder(orderId)
        } else {
            throw SecurityException("You are not logged now")
        }
    }

    fun getOrderStatus(orderId: Int): OrderStatus? {
        if (isLoggedNow) {
            return orderSystem.getOrderStatus(orderId)
        } else {
            throw SecurityException("You are not logged now")
        }
    }

    fun payOrder(orderId: Int) {
        if (isLoggedNow) {
            orderSystem.payOrder(orderId)
            increaseLevel()
        } else {
            throw SecurityException("You are not logged now")
        }
    }

    private fun increaseLevel() {
        ++counterOrders
        if (counterOrders in 11..29) {
            visitorStatus = UserStatus.Medium
        } else if (counterOrders > 30) {
            visitorStatus = UserStatus.Lover
        }
    }

    fun leaveFeedbackAboutOrder(orderId: Int, stars: Int, comment: String) {
        if (isLoggedNow) {
            orderSystem.setReviewToOrder(orderId, stars, comment)
        } else {
            throw SecurityException("You are not logged now")
        }
    }

    fun getMenu(): String {
        return orderSystem.menuObj.getString()
    }

}