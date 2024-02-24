package restaurant.usersystem

import restaurant.dish.DishParams
import restaurant.order.OrderSystem
import java.time.Duration

class Admin(id: Int, login: String, password: String, orderSystem: OrderSystem) :
    User(id, login, password, UserRole.Admin, orderSystem) {
    fun addDishToMenu(name : String, price : Int, timeProduction : Duration, count: Int = 1) : Int {
        return orderSystem.menuObj.addDishToMenu(name, price, timeProduction, count)
    }

    fun removeDishFromMenu(dishId: Int) {
        orderSystem.menuObj.removeDishFromMenu(dishId)
    }

    fun setParamToDish(dishId: Int, params: DishParams, type: Any) {
        orderSystem.menuObj.setParamToDish(dishId, params, type)
    }

    fun increaseTheNumberOfDish(dishId : Int, delta : Int) {
        orderSystem.increaseTheNumber(dishId, delta)
    }

    fun getStatistics() {

    }

}