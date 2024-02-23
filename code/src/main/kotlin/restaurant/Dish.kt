package restaurant

import java.time.Duration

data class Dish(val name : String, var amount : Int, var price : Int, var timeProduction : Duration)
