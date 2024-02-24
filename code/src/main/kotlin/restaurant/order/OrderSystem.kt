package restaurant.order

import restaurant.Logger
import restaurant.dish.Dish
import restaurant.dish.Menu
import java.util.PriorityQueue

class OrderSystem {
    private val comparator: Comparator<Order> = compareBy { it.level }
    private val waitingOrders = PriorityQueue(comparator)
    private val allOrders = mutableListOf<Order>()
    val menuObj = Menu()
    private var orderIdGetter = 0

    private val getOrderId: Int
        get() {
            ++orderIdGetter
            return orderIdGetter
        }

    private fun getOrderFromQueue(): Order? {
        if (waitingOrders.isEmpty()) {
            return null
        }
        val result = waitingOrders.first()
        waitingOrders.remove()
        return result
    }

    private fun getOrderById(id: Int): Order? {
        return allOrders.find { it.orderId == id }
    }

    @JvmName("AddOrderIntToInt")
    fun addOrder(list: MutableMap<Int, Int>, level: ImportanceLevel, userId: Int) : Int {
        val idForThisOrder = getOrderId
        val order = Order(menuObj.getDishMapFromIds(list), level, menuObj, userId, idForThisOrder)
        allOrders.add(order)
        waitingOrders.add(order)
        return idForThisOrder
    }

    @JvmName("AddOrderStringToInt")
    fun addOrder(list: MutableMap<String, Int>, level: ImportanceLevel, userId: Int) : Int {
        val idForThisOrder = getOrderId
        val order = Order(menuObj.getDishMapFromNames(list), level, menuObj, userId, idForThisOrder)
        allOrders.add(order)
        waitingOrders.add(order)
        return idForThisOrder
    }

    fun addToExistedOrder(orderId: Int, dish: Dish, amount: Int = 1) {
        Logger.writeToLog("Try to add 1 dish to existed order with id $orderId")
        val order = getOrderById(orderId)
        if (order == null) {
            Logger.writeToLogResult("This order doesn't exists.", Logger.Status.ERROR)
        } else {
            order.addDish(dish, amount)
        }
    }

    fun addToExistedOrder(orderId: Int, listOfDishes: MutableList<Dish>, amount: MutableList<Int> = mutableListOf()) {
        Logger.writeToLog("Try to add 1 dish to existed order with id $orderId")
        val order = getOrderById(orderId)
        if (order == null) {
            Logger.writeToLogResult("This order doesn't exists.", Logger.Status.ERROR)
        } else {
            for (i in 0..listOfDishes.size) {
                if (listOfDishes.size != amount.size) {
                    order.addDish(listOfDishes[i], 1)
                } else {
                    order.addDish(listOfDishes[i], amount[i])
                }
            }
        }
    }

    fun cancelOrder(orderId : Int) {
        val order = getOrderById(orderId)
        if (order == null) {
            Logger.writeToLogResult("Order doesn't exists (you cannot cancel order that doesn't exists)", Logger.Status.ERROR)
        } else {
            order.cancelOrder()
        }
    }

    fun getOrderStatus(orderId : Int) : OrderStatus? {
        val order = getOrderById(orderId)
        return if (order == null) {
            Logger.writeToLogResult("Order doesn't exists (you cannot cancel order that doesn't exists)", Logger.Status.ERROR)
            null
        } else {
            Logger.writeToLogResult("OrderStatus is ${order.getStatus()}", Logger.Status.OK)
            order.getStatus()
        }
    }

    fun payOrder(orderId : Int) {
        val order = getOrderById(orderId)
        if (order != null) {
            order.payOrder()
        } else {
            Logger.writeToLogResult("Order with this id doesn't exists", Logger.Status.ERROR)
        }
    }

    fun setReviewToOrder(orderId : Int, stars : Int, comment : String) {
        val order = getOrderById(orderId)
        if (order != null) {
            order.setReview(stars, comment)
        } else {
            Logger.writeToLogResult("Order with this id doesn't exists", Logger.Status.ERROR)
        }
    }
}