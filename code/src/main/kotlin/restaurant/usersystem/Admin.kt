package restaurant.usersystem

import kotlinx.serialization.Serializable
import restaurant.dish.DishParams
import kotlin.time.Duration

@Serializable
class Admin(
    val id: Int,
    private val login: String,
    private val password: String
) : User() {
    init {
        setRole(UserRole.Admin)
    }

    override fun compareData(log: String, passw: String): Boolean {
        return login == log && password == passw
    }

    /**
     * Add dish to menu
     *
     * @param name: name of dish
     * @param price: price of dish
     * @param timeProduction: time of dish production
     * @param count: number of available dishes in restaurant
     * @return id of dish in menu
     */
    fun addDishToMenu(name: String, price: Int, timeProduction: Duration, count: Int = 1): Int {
        if (isLoggedNow) {
            return orderSystem.menuObj.addDishToMenu(name, price, timeProduction, count)
        } else {
            throw SecurityException("You are not logged now")
        }
    }

    /**
     * Remove dish from menu
     * @param dishId: dish that should be deleted from menu
     */
    fun removeDishFromMenu(dishId: Int) {
        if (isLoggedNow) {
            orderSystem.menuObj.removeDishFromMenu(dishId)
        } else {
            throw SecurityException("You are not logged now")
        }
    }

    /**
     * Set param to dish
     *
     * @param dishId: id of dish, that should edit
     * @param params: object of enum DishParams: Name, Price or TimeProduction
     * @param type: value of params object
     */
    fun setParamToDish(dishId: Int, params: DishParams, type: Any) {
        if (isLoggedNow) {
            orderSystem.menuObj.setParamToDish(dishId, params, type)
        } else {
            throw SecurityException("You are not logged now")
        }
    }

    /**
     * Increase / decrease the number of dish by delta
     *
     * @param dishId: id of dish, that should edit
     * @param delta: a number of portions of dish that system need to delete / add to the menu
     */
    fun increaseTheNumberOfDish(dishId: Int, delta: Int) {
        if (isLoggedNow) {
            orderSystem.increaseTheNumber(dishId, delta)
        } else {
            throw SecurityException("You are not logged now")
        }
    }

    /**
     * Get statistics of the restaurant
     *
     * @return string-presentation of statistics
     */
    fun getStatistics(): String {
        return if (isLoggedNow) {
            orderSystem.getStatistics()
        } else {
            orderSystem.getStatistics()
        }
    }
}