package restaurant.order

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.encodeToString
import restaurant.Logger
import restaurant.Serializer
import restaurant.dish.Dish

@Serializable
class Order(
    private var list: MutableMap<Dish, Int>,
    internal var level: ImportanceLevel,
    internal var userId: Int,
    var orderId: Int,
) {
    @Serializable
    private var status = OrderStatus.New

    @Transient
    private lateinit var associatedThread: Thread

    @Serializable
    private var review: Review = Review()

    /**
     * Add dish to current existed order
     *
     * @param newDish: object of Dish that you need to add to your order
     * @param amount: the number of portions of dish you need
     */
    internal fun addDish(newDish: Dish, amount: Int = 1) {
        if (status == OrderStatus.Accepted) {
            if (list[newDish] != null) {
                Logger.writeToLogResult(
                    "Trying to add dish to order $orderId for user #${userId} : ${newDish.name}",
                    Logger.Status.OK
                )
                if (list.containsKey(newDish)) {
                    list[newDish] = list[newDish]!! + amount
                } else {
                    list[newDish] = amount
                }
            }
        } else {
            Logger.writeToLogResult(
                "Trying to add dish to order $orderId for user #${userId}: ${newDish.name}. It's not accepted!",
                Logger.Status.ERROR
            )
            throw SecurityException("This order is not accepted!")
        }
    }

    /**
     * Accept order
     *
     * @param result
     * @return: result of accepting
     */
    internal fun acceptOrder(result: Boolean): Boolean {
        return if (!result) {
            status = OrderStatus.Canceled
            false
        } else {
            status = OrderStatus.Accepted
            true
        }
    }

    /**
     * Calculates time of order production
     * @return time of order production
     */
    internal fun getMaxTime(): Long {
        return list.keys.maxBy { it.timeProduction }.timeProduction.inWholeSeconds
    }


    /**
     * Function of starting order,
     * creates new thread (it sleeps for getMaxTime() seconds) and managing of queue of orders
     * @throws SecurityException if order is already cooking
     */
    internal fun startOrder() {
        if (status == OrderStatus.Accepted) {
            associatedThread = Thread {
                OrderSystem.numberOfThreads.incrementAndGet()
                try {
                    Logger.writeToLog("Start cooking order $orderId...")
                    status = OrderStatus.Cooking
                    Thread.sleep(getMaxTime() * 1000)
                    Logger.writeToLogResult("Order $orderId for user #${userId} is ready!", Logger.Status.OK)
                    status = OrderStatus.Ready
                    Serializer.write(Serializer.json.encodeToString(OrderSystem.allOrders), Serializer.allOrdersFile)
                } catch (_: Exception) {
                    Logger.writeToLogResult("Cancel the order $orderId for user #${userId}.", Logger.Status.OK)
                    status = OrderStatus.Canceled
                }
                OrderSystem.numberOfThreads.decrementAndGet()
                OrderSystem.getOrderFromQueue()
            }
            associatedThread.start()
        } else {
            Logger.writeToLogResult("Order $orderId for user #${userId} is not accepted!", Logger.Status.ERROR)
            throw SecurityException("Order $orderId for user #${userId} is not accepted!")
        }
    }

    /**
     * Cancel order, if it is cooking now
     * @throws SecurityException if order is not cooking now
     */
    fun cancelOrder() {
        if (status == OrderStatus.Cooking) {
            associatedThread.interrupt()
        } else {
            Logger.writeToLogResult("Order $orderId is not cooking!", Logger.Status.ERROR)
            throw SecurityException("Order $orderId is not cooking!")
        }
    }

    /**
     * Change the status of order to Payed. In real systems,
     * this method can call other PaymentSystem, but now it just believes that user have enough money in his card
     * @return the total bill of order
     * @throws SecurityException if order is now already payed or maybe cooking
     */
    fun payOrder(): Int {
        if (status == OrderStatus.Ready) {
            val sum = sumOfBill
            Logger.writeToLogResult("Order $orderId for user #${userId} for sum = $sum has paid.", Logger.Status.OK)
            status = OrderStatus.Payed
            return sum
        } else {
            Logger.writeToLogResult("You cannot pay the order $orderId now, now order is $status", Logger.Status.ERROR)
            throw SecurityException("You cannot pay the order $orderId now, now order is $status")
        }
    }

    /**
     * Get status of order
     * @return order status
     */
    fun getStatus(): OrderStatus {
        return status
    }

    /**
     * Set review to this order
     * @throws SecurityException: if status of order is not Payed or Ready
     */
    fun setReview(stars: Int, comment: String) {
        if (status == OrderStatus.Payed || status == OrderStatus.Ready) {
            Logger.writeToLog("Visitor $userId left the review with $stars stars for the order $orderId")
            review = Review(stars, comment)
        } else {
            throw SecurityException("You cannot set the review on the order now.")
        }
    }

    internal val getNumberOfDishes
        get() = list.size

    internal val sumOfBill : Int
        get() {
            var sum = 0
            for ((dish, amount) in list) {
                sum += dish.price * amount
            }
            return sum
        }

    internal val getMarkIfItExists: Int?
        get() {
            if (review.isInitialized) {
                return review.stars
            }
            return null
        }

}