package restaurant.dish

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import restaurant.Logger
import restaurant.Serializer
import kotlin.time.Duration

@Serializable
class Menu(@Serializable private var dishList: MutableMap<Dish, Int> = mutableMapOf()) {
    init {
        tryToDeserialize()
    }

    @Serializable
    private var dishIdGetter = 0

    private val getDishId: Int
        get() {
            ++dishIdGetter
            return dishIdGetter
        }

    internal val getCurrentDishesId: List<Int>
        get() = dishList.map { it.key.dishId }

    private fun checkPossibility(list: MutableMap<Dish, Int>): Boolean {
        tryToDeserialize()
        Logger.writeToLog("checking possibility to accept the order...")
        for (element in list) {
            val checker =
                dishList.filter { it.key.dishId == element.key.dishId && it.key.name == element.key.name }.keys.firstOrNull()
            if (checker == null) {
                Logger.writeToLogResult("Dish with name ${element.key} doesn't exists", Logger.Status.ERROR)
                return false
            } else if (dishList[checker]!! < element.value) {
                Logger.writeToLogResult(
                    "There is not enough quantity of dish with name ${element.key}. need ${element.value}, have ${dishList[checker]!!}",
                    Logger.Status.ERROR
                )
                return false
            }
        }

        return true
    }

    fun acceptOrder(order: MutableMap<Dish, Int>): Boolean {
        tryToDeserialize()
        Logger.writeToLog("Accepting the order...")
        val status = checkPossibility(order)
        if (!status) {
            Logger.writeToLogResult("Order hasn't accepted!", Logger.Status.ERROR)
            return false
        }
        for (element in order) {
            dishList[element.key]?.minus(element.value)
        }
        Logger.writeToLogResult("Order has accepted!", Logger.Status.OK)
        serialize()
        return true
    }

    fun addDishToMenu(name: String, price: Int, timeProduction: Duration, amount: Int): Int {
        val id = getDishId

        return if (dishList.keys.none { it.name == name }) {
            val dish = Dish(id, name, price, timeProduction)
            dishList[dish] = amount
            serialize()
            Logger.writeToLogResult(
                "New dish with NAME=${dish.name} and AMOUNT=$amount added to menu",
                Logger.Status.OK
            )
            id
        } else {
            --dishIdGetter
            serialize()
            Logger.writeToLogResult("Dish with this name ($name) already exists.", Logger.Status.ERROR)
            dishList.keys.firstOrNull { it.name == name }!!.dishId
        }
    }

    fun removeDishFromMenu(dishId: Int) {
        tryToDeserialize()
        val dish = getDishById(dishId)

        if (dish != null) {
            Logger.writeToLogResult("Try to remove dish with name = ${dish.name} from menu", Logger.Status.OK)
            dishList.remove(dish)
        } else {
            Logger.writeToLogResult("Try to remove dish with name = NULL from menu", Logger.Status.ERROR)
        }
        serialize()
    }

    private fun contains(dish: Int): Boolean {
        return dishList.filter { it.key.dishId == dish }.isNotEmpty()
    }

    fun getDishById(dish: Int): Dish? {
        tryToDeserialize()
        return dishList.filter { it.key.dishId == dish }.keys.firstOrNull()
    }

    fun setParamToDish(dishName: Int, params: DishParams, type: Any) {
        tryToDeserialize()
        if (contains(dishName)) {
            val dish = getDishById(dishName)
            when (params) {
                DishParams.Name -> {
                    if (type is String) {
                        Logger.writeToLogResult("Try to set parameter $params to $dishName...", Logger.Status.OK)
                        val copyValue = dishList[dish]
                        dish?.name = type
                        dishList.remove(dish)
                        dishList[dish!!] = copyValue!!
                    } else {
                        Logger.writeToLogResult("Incorrect type of parameter", Logger.Status.ERROR)
                    }
                }

                DishParams.Price -> {
                    if (type is Int) {
                        Logger.writeToLogResult("Try to set parameter $params to $dishName...", Logger.Status.OK)
                        val copyValue = dishList[dish]
                        dish?.price = type
                        dishList.remove(dish)
                        dishList[dish!!] = copyValue!!
                    } else {
                        Logger.writeToLogResult("Incorrect type of parameter", Logger.Status.ERROR)
                    }
                }

                DishParams.TimeProduction -> {
                    if (type is Duration) {
                        Logger.writeToLogResult("Try to set parameter $params to $dishName...", Logger.Status.OK)
                        val copyValue = dishList[dish]
                        dish?.timeProduction = type
                        dishList.remove(dish)
                        dishList[dish!!] = copyValue!!
                    } else {
                        Logger.writeToLogResult("Incorrect type of parameter", Logger.Status.ERROR)
                    }
                }
            }
        } else {
            Logger.writeToLogResult(
                "Dish with id = $dishName doesn't contains in dish list of restaurant.",
                Logger.Status.ERROR
            )
        }
        serialize()
    }

    fun getDishMapFromIds(list: MutableMap<Int, Int>): MutableMap<Dish, Int> {
        val result = mutableMapOf<Dish, Int>()

        for (idx in 0 until list.keys.size) {
            val obj = dishList.filter { it.key.dishId == list.keys.elementAt(idx) }.keys.firstOrNull()
            if (obj == null) {
                Logger.writeToLog("")
            } else {
                result[obj] = list.values.elementAt(idx)
            }
        }
        return result
    }

    fun getDishMapFromNames(list: MutableMap<String, Int>): MutableMap<Dish, Int> {
        val result = mutableMapOf<Dish, Int>()

        for (idx in 0 until list.keys.size) {
            val obj = dishList.filter { it.key.name == list.keys.elementAt(idx) }.keys.firstOrNull()
            if (obj == null) {
                Logger.writeToLog("")
            } else {
                result[obj] = list.values.elementAt(idx)
            }
        }
        return result
    }

    fun increaseAmountOfDish(dishId: Int, delta: Int) {
        tryToDeserialize()
        val obj = getDishById(dishId)

        if (obj == null) {
            Logger.writeToLogResult("Trying to increase amount of dish with name = NULL in $delta", Logger.Status.ERROR)
        } else {
            dishList[obj] = dishList[obj]!! + delta
            Logger.writeToLogResult(
                "Trying to increase amount of dish with name = ${obj.name} in $delta",
                Logger.Status.OK
            )
        }
        serialize()
    }

    private fun serialize() {
        Serializer.write(Serializer.json.encodeToString(dishList), "data/menu_dishs.ser")
        Serializer.write(Serializer.json.encodeToString(dishIdGetter), "data/last_id_menu.ser")
    }

    private fun tryToDeserialize() {
        try {
            dishList = Serializer.json.decodeFromString(Serializer.read(Serializer.dishListFile)!!)
        } catch (ex: Exception) {
            Logger.writeToLog("DishList deserializator: ${ex.message.toString()}")
        }

        try {
            dishIdGetter = Serializer.json.decodeFromString(Serializer.read(Serializer.dishIdGetterFile)!!)
        } catch (ex: Exception) {
            Logger.writeToLog("DishIdGetter deserializator: ${ex.message.toString()}")
        }
    }

    fun getString(): String {
        var result = ""
        for (dish in dishList.keys) {
            result += "#${dish.dishId}\t${dish.name}\tCosts:${dish.price}\tTime: ${dish.timeProduction}\n"
        }
        return result
    }
}