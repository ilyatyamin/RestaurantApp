package restaurant.dish

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import restaurant.Logger
import restaurant.Serializer
import kotlin.math.abs
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

    /** Check the possibility to accept the order.
     * @return is it possible to accept the order
     * @throws SecurityException if there are no possibility to accept the order **/
    private fun checkPossibility(list: MutableMap<Dish, Int>): Boolean {
        tryToDeserialize()
        Logger.writeToLog("checking possibility to accept the order...")
        for (element in list) {
            val checker =
                dishList.filter { it.key.dishId == element.key.dishId && it.key.name == element.key.name }.keys.firstOrNull()
            if (checker == null) {
                Logger.writeToLogResult("Dish with name ${element.key} doesn't exists", Logger.Status.ERROR)
                throw SecurityException("Dish with name ${element.key} doesn't exists")
            } else if (dishList[checker]!! < element.value) {
                Logger.writeToLogResult(
                    "There is not enough quantity of dish with name ${element.key}. need ${element.value}, have ${dishList[checker]!!}",
                    Logger.Status.ERROR
                )
                throw IndexOutOfBoundsException("There is not enough quantity of dish with name ${element.key}. need ${element.value}, have ${dishList[checker]!!}")
            } else if (element.value <= 0) {
                throw IndexOutOfBoundsException("You cannot buy less than zero of this dish.")
            }
        }

        return true
    }

    /** The main method to accept the order.
     * @return is it possible to accept the order
     * @throws SecurityException if order cannot be accepted **/
    fun acceptOrder(order: MutableMap<Dish, Int>): Boolean {
        tryToDeserialize()
        // Check possibility to accept the order
        val status = checkPossibility(order)
        if (!status) {
            Logger.writeToLogResult("Order hasn't accepted!", Logger.Status.ERROR)
            throw SecurityException("Try to remove dish with name = NULL from menu")
        }
        for (element in order) {
            dishList[element.key] = dishList[element.key]!! - (element.value)
        }
        Logger.writeToLogResult("Order has accepted!", Logger.Status.OK)
        serialize()
        return true
    }

    /**
     * Admin method: add dish to menu. You need name, price, timeProdiction of type Duration and amount of this dish in your restaurant
     * @return id of dish in menu
     */
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

    /**
     * Removes dish from menu by dishId
     * @throws SecurityException if dish with dishId doesn't exists
     */
    fun removeDishFromMenu(dishId: Int) {
        tryToDeserialize()
        val dish = getDishById(dishId)

        if (dish != null) {
            Logger.writeToLogResult("Try to remove dish with name = ${dish.name} from menu", Logger.Status.OK)
            dishList.remove(dish)
        } else {
            Logger.writeToLogResult("Try to remove dish with name = NULL from menu", Logger.Status.ERROR)
            throw SecurityException("Try to remove dish with name = NULL from menu")
        }
        serialize()
    }

    private fun contains(dish: Int): Boolean {
        return dishList.filter { it.key.dishId == dish }.isNotEmpty()
    }

    /**
     * @return Dish object in dependent on inputted ID
     */
    fun getDishById(dish: Int): Dish? {
        tryToDeserialize()
        return dishList.filter { it.key.dishId == dish }.keys.firstOrNull()
    }

    /**
     * Set params to dish: Name of dish, Price, TimeProduction
     *
     * @param dishName: id of dish
     * @param params: type of DishParams
     * @param type: what you need to replace in menu
     */
    fun setParamToDish(dishName: Int, params: DishParams, type: Any) {
        tryToDeserialize()
        if (contains(dishName)) {
            when (params) {
                DishParams.Name -> {
                    if (type is String) {
                        Logger.writeToLogResult("Try to set parameter $params to $dishName...", Logger.Status.OK)
                        dishList.keys.find { it.dishId == dishName }?.name = type
                    } else {
                        Logger.writeToLogResult("Incorrect type of parameter", Logger.Status.ERROR)
                        throw SecurityException("Incorrect type of parameter")
                    }
                }

                DishParams.Price -> {
                    when (type) {
                        is Int -> {
                            if (type < 0) {
                                throw SecurityException("Price cannot be less than zero.")
                            }
                            Logger.writeToLogResult("Try to set parameter $params to $dishName...", Logger.Status.OK)
                            dishList.keys.find { it.dishId == dishName }?.price = type
                        }

                        is String -> {
                            if (type.toInt() < 0) {
                                throw SecurityException("Price cannot be less than zero.")
                            }
                            Logger.writeToLogResult("Try to set parameter $params to $dishName...", Logger.Status.OK)
                            dishList.keys.find { it.dishId == dishName }?.price = type.toInt()
                        }

                        else -> {
                            Logger.writeToLogResult("Incorrect type of parameter", Logger.Status.ERROR)
                            throw SecurityException("Incorrect type of parameter")
                        }
                    }
                }

                DishParams.TimeProduction -> {
                    when (type) {
                        is Duration -> {
                            Logger.writeToLogResult("Try to set parameter $params to $dishName...", Logger.Status.OK)
                            dishList.keys.find { it.dishId == dishName }?.timeProduction = type
                        }

                        is String -> {
                            Logger.writeToLogResult("Try to set parameter $params to $dishName...", Logger.Status.OK)
                            dishList.keys.find { it.dishId == dishName }?.timeProduction = Duration.parse(type)
                        }

                        else -> {
                            Logger.writeToLogResult("Incorrect type of parameter", Logger.Status.ERROR)
                            throw SecurityException("Incorrect type of parameter")
                        }
                    }
                }
            }
        } else {
            Logger.writeToLogResult(
                "Dish with id = $dishName doesn't contains in dish list of restaurant.",
                Logger.Status.ERROR
            )
            throw SecurityException("Dish with id = $dishName doesn't contains in dish list of restaurant.")
        }
        serialize()
    }

    /**
     * Transforms map of ids to map of Dish objects
     *
     * @param list: map, where key is ID of Dish and value is a number of portions of this dish
     * @return map, where key is object of Dish and value is a number of portions of this dish
     */
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

    /**
     * Increases / Decreases amount of dish in menu by delta.
     *
     * @param dishId: id of Menu
     * @param delta: a number that you have to add to do smth
     * @throws SecurityException: if object doesn't exists
     */
    fun increaseAmountOfDish(dishId: Int, delta: Int) {
        tryToDeserialize()
        val obj = getDishById(dishId)

        if (obj == null) {
            Logger.writeToLogResult("Trying to increase amount of dish with name = NULL in $delta", Logger.Status.ERROR)
            throw SecurityException("This object doesn't exists.")
        } else {
            if (delta < 0 && dishList[obj]!! >= abs(delta)) {
                dishList[obj] = dishList[obj]!! + delta
            } else if (delta > 0) {
                dishList[obj] = dishList[obj]!! + delta
            } else {
                Logger.writeToLogResult(
                    "Trying to increase amount of dish with name = ${obj.name} in $delta",
                    Logger.Status.OK
                )
            }
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

    /**
     * Get string presentation like String
     * @return string-presentation of menu
     */
    fun getString(): String {
        var result =
            "${"ID".padEnd(10)}${"Name".padEnd(20)}${"Cost".padEnd(20)}${"Time".padEnd(20)}${"Available".padEnd(20)}\n"
        for ((dish, value) in dishList) {
            result += "${dish.dishId.toString().padEnd(10)}${dish.name.padEnd(20)}${
                dish.price.toString().padEnd(20)
            }${dish.timeProduction.toString().padEnd(20)}${value.toString().padEnd(20)}\n"
        }
        return result
    }
}