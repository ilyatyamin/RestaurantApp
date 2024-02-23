package restaurant

import java.sql.Time

class Order(private var time: Time, private var list: MutableList<Dish>, private var level: ImportanceLevel) {
    var status = OrderStatus.Accepted

    fun addDish(newDish: Dish) {
        list.add(newDish)
    }
}