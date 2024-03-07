package restaurant.order

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

    fun getStatistics(): String {
        return """
            Statistics:
            1. The average mark for orders: $averageMark
            2. The average number of dishes in 1 order: $averageNumberOfDishes    
        """.trimIndent()
    }
}