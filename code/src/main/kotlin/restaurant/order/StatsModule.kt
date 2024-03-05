package restaurant.order

import restaurant.Logger

internal class StatsModule(private val allOrders: MutableList<Order>) {
    private val averageNumberOfDishes: Double
        get() {
            val list = mutableListOf<Int>()
            for (order in allOrders) {
                list.add(order.getNumberOfDishes)
            }
            return list.sum().toDouble() / list.size
        }

    private val averageMark: Double
        get() {
            val list = mutableListOf<Int>()
            for (order in allOrders) {
                val value = order.getMarkIfItExists
                if (value != null) {
                    list.add(value)
                }
            }
            return list.sum().toDouble() / list.size
        }

    private val mostPopularDish: String?
        get() {
            val list = mutableMapOf<String, Int>()
            for (order in allOrders) {
                val value = order.getMostPopularDish
                try {
                    list[value.first!!] = list[value.first]!! + (value.second ?: 0)
                } catch (ex : Exception) {
                    Logger.writeToLog("Try to get most popular dish: ${ex.message}")
                }

            }
            return list.maxByOrNull { it.value }?.key
        }

    fun getStatistics(): String {
        return """
            Statistics:
            1. The average mark for orders: $averageMark
            2. The average number of dishes in 1 order: $averageNumberOfDishes
            3. The most popular dish is: $mostPopularDish        
        """.trimIndent()
    }
}