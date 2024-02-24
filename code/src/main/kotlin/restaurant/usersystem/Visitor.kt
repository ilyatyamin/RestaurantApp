package restaurant.usersystem

import restaurant.order.ImportanceLevel
import restaurant.order.OrderStatus
import restaurant.order.OrderSystem

class Visitor(id : Int, login: String, password: String, orderSystem: OrderSystem) :
    User(id, login, password, UserRole.Visitor, orderSystem) {
    private var counterOrders = 0
    private var visitorStatus = UserStatus.Beginner

    private fun matchStatusWithLevel() : ImportanceLevel {
        return when(visitorStatus) {
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
    fun makeOrder(listOfOrder: MutableMap<Int, Int>) : Int {
        return orderSystem.addOrder(listOfOrder, matchStatusWithLevel(), id)
    }

    @JvmName("MakeOrderByString")
    fun makeOrder(listOfOrder: MutableMap<String, Int>) : Int {
        return orderSystem.addOrder(listOfOrder, matchStatusWithLevel(), id)
    }

    fun addToOrder(orderId : Int, dishId : Int) {
        orderSystem.addToExistedOrder(orderId, dishId)
    }

    fun addToOrder(orderId : Int, dishes : MutableList<Int>) {
        orderSystem.addToExistedOrder(orderId, dishes)
    }

    fun cancelOrder(orderId : Int) {
        orderSystem.cancelOrder(orderId)
    }
    fun getOrderStatus(orderId : Int) : OrderStatus? {
        return orderSystem.getOrderStatus(orderId)
    }
    fun payOrder(orderId : Int) {
        orderSystem.payOrder(orderId)
    }

    fun leaveFeedbackAboutOrder(orderId : Int, stars : Int, comment : String) {
        orderSystem.setReviewToOrder(orderId, stars, comment)
    }

}