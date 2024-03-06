import DishParams.DishParams

class ConsoleSystem {
    private val greenColor = "\u001b[32m"
    private val resetColor = "\u001b[0m" // to reset color to the default
    private var client = Client()

    var isLogged = false
    var userType: Type = Type.None

    enum class Type { Admin, Visitor, None }

    init {
        showRegistrateMenu()
    }

    private fun showRegistrateMenu(): String {
        return """
            ${makeTextGreen("Пожалуйста, зарегистрируйтесь.")}
            1. Войти как пользователь.
            2. Войти как администратор.
            3. Зарегистрироваться как пользователь.
            4. Зарегистрироваться как администратор.
            5. Посмотреть меню ресторана.
            -1. Выйти из приложения.
        """.trimIndent()
    }

    fun makeChoiceRegistrateMenu() {
        try {
            println(showRegistrateMenu())
            val inp = fromStringToCorrectChoice(readln(), 1, 5)
            when (inp) {
                1 -> { // войти как пользователь
                    val rawData = inputAccountData()
                    val status = client.loginUser(rawData.first, rawData.second)
                    if (status == "OK") {
                        userType = Type.Visitor
                        isLogged = true
                    }
                    println("Result: ${makeTextGreen(status)}")
                }

                2 -> {
                    val rawData = inputAccountData()
                    val status = client.loginAdmin(rawData.first, rawData.second)
                    if (status == "OK") {
                        userType = Type.Admin
                        isLogged = true
                    }
                    println("Result: ${makeTextGreen(status)}")
                }

                3 -> {
                    val rawData = inputAccountData()
                    val status = client.registerNewVisitor(rawData.first, rawData.second)
                    println("Result: ${makeTextGreen(status)}")
                }

                4 -> {
                    val rawData = inputAccountData()
                    val status = client.registerNewAdmin(rawData.first, rawData.second)
                    println("Result: ${makeTextGreen(status)}")
                }

                5 -> {
                    println(makeTextGreen(client.getMenu()))
                }

                -1 -> {
                    throw InterruptedException()
                }
            }
        } catch (ex: Exception) {
            println("Exception! ${ex.message.toString()}")
        }
    }

    private fun showAdminMenu(): String {
        return """
            ${makeTextGreen("Меню администратора:")}
            1. Добавить блюдо в меню.
            2. Убрать блюдо из меню по его id. 
            3. Изменить характеристику блюда по его id.
            4. Увеличить количество единиц блюда по его id.
            5. Получить статистику по ресторану.
            6. Посмотреть меню ресторана.
            7. Выйти из аккаунта.
            -1. Выйти из приложения.
        """.trimIndent()
    }

    fun makeChoiceAdminMenu() {
        try {
            println(showAdminMenu())
            val choice = fromStringToCorrectChoice(readln(), 1, 8)
            when (choice) {
                1 -> { // добавить блюдо в меню
                    print("Введите название блюда: ")
                    val name = readln()
                    print("Введите стоимость блюда: ")
                    val price = fromStringToCorrectChoice(readln(), 0, Int.MAX_VALUE)
                    print("Введите длительность готовки. Пожалуйста, соблюдайте cледующий формат: использовать h, m, s. Например, 1h 30m.")
                    val duration = readln()
                    print("Введите доступное количество блюда. При некорректном вводе значение равно 1.")
                    val count = fromStringToIntWithOne(readln())

                    val result = client.addDishToMenu(name, price, duration, count)
                    println("ID блюда в меню: ${makeTextGreen(result)}")
                }

                2 -> { // убрать блюдо из меню по его id
                    print("Введите ID блюда: ")
                    val id = fromStringToCorrectChoice(readln(), 0, Int.MAX_VALUE)

                    val result = client.removeDishFromMenu(id)
                    println("Result: ${makeTextGreen(result)}")
                }

                3 -> { // изменить характеристику блюда по его id
                    print("Введите ID блюда: ")
                    val id = fromStringToCorrectChoice(readln(), 0, Int.MAX_VALUE)

                    print("Введите то, что нужно изменить: Name, Price, TimeProduction: ")
                    val possibleType = readln().lowercase()
                    if (possibleType !in mutableListOf("name", "price", "timeproduction")) {
                        throw IndexOutOfBoundsException("Неверный ввод.")
                    }
                    var type: DishParams = DishParams.Name
                    when (possibleType) {
                        "name" -> {
                            type = DishParams.Name
                        }

                        "price" -> {
                            type = DishParams.Price
                        }

                        "timeproduction" -> {
                            type = DishParams.TimeProduction
                        }
                    }

                    print("Введите то, на что нужно изменить: ")
                    val possibleChange = readln()

                    val result = client.setParamToDish(id, type, possibleChange)
                    println("Result: ${makeTextGreen(result)}")
                }

                4 -> { // увеличить кол-во блюда на delta
                    print("Введите ID блюда: ")
                    val id = fromStringToCorrectChoice(readln(), 0, Int.MAX_VALUE)
                    print("Введите на сколько пополнить его запас: ")
                    val delta = fromStringToCorrectChoice(readln(), 0, Int.MAX_VALUE)

                    val result = client.increaseTheNumberOfDish(id, delta)
                    println("Result: ${makeTextGreen(result)}")
                }

                5 -> { // Получить статистику по ресторану.
                    println("Result: ${client.getStatistics()}")
                }

                6 -> { // получить меню ресторана
                    println(makeTextGreen(client.getMenu()))
                }

                7 -> {
                    isLogged = false
                    userType = Type.None
                    client.exitAdmin()
                }

                -1 -> {
                    throw InterruptedException()
                }
            }
        } catch (ex: Exception) {
            println(makeTextGreen(ex.message.toString()))
        }
    }


    private fun showVisitorMenu(): String {
        return """
            ${makeTextGreen("Меню посетителя:")}
            1. Добавить новый заказ.
            2. Добавить блюдо / блюда в существующий заказ.
            3. Получить статус заказа по его ID.
            4. Отменить заказ по его ID.
            5. Заплатить за заказ.
            6. Оставить отзыв о заказе.
            7. Посмотреть меню ресторана.
            8. Выйти из аккаунта.
            -1. Выйти из приложения.
        """.trimIndent()
    }

    fun makeChoiceVisitorMenu() {
        try {
            println(showVisitorMenu())
            val choice = fromStringToCorrectChoice(readln(), 1, 9)
            when (choice) {
                1 -> {
                    // получить новый заказ
                    println("----- Меню -----\n${client.getMenu()}")
                    val list = inputOrderList()
                    val result = client.makeOrder(list)
                    println("Your order's id: ${makeTextGreen(result)}")
                }

                2 -> {
                    // добавить блюдо в существующий заказ
                    print("Введите ID вашего заказа: ")
                    val orderId = fromStringToCorrectChoice(readln(), 1, Int.MAX_VALUE)

                    // Чекер, что заказ еще готовится
                    if (client.getOrderStatus(orderId) != "Preparing") {
                        println("Your order is not preparing")
                    } else {
                        val list = inputOrderList()
                        val result = client.addToOrder(orderId, list)
                        println("Status: ${makeTextGreen(result)}")
                    }
                }

                3 -> {
                    // Получить статус заказа по его ID.
                    print("Введите ID вашего заказа: ")
                    val orderId = fromStringToCorrectChoice(readln(), 1, Int.MAX_VALUE)
                    val result = client.getOrderStatus(orderId)
                    println("Status: ${makeTextGreen(result)}")
                }

                4 -> {
                    // отменить за заказ
                    print("Введите ID вашего заказа: ")
                    val orderId = fromStringToCorrectChoice(readln(), 1, Int.MAX_VALUE)
                    val result = client.cancelOrder(orderId)
                    println("Status: ${makeTextGreen(result)}")
                }

                5 -> {
                    // заплатить за заказ
                    print("Введите ID вашего заказа: ")
                    val orderId = fromStringToCorrectChoice(readln(), 1, Int.MAX_VALUE)
                    val result = client.payOrder(orderId)
                    println("Status: ${makeTextGreen(result)}")
                }

                6 -> {
                    print("Введите ID вашего заказа: ")
                    val orderId = fromStringToCorrectChoice(readln(), 1, Int.MAX_VALUE)
                    print("Сколько баллов вы ставите заказу (от 1 до 5): ")
                    val stars = fromStringToCorrectChoice(readln(), 1, 5)
                    print("Ваш комментарий: ")
                    val comment = readln()

                    val result = client.leaveFeedbackAboutOrder(orderId, stars, comment)
                    println("Status: ${makeTextGreen(result)}")
                }

                7 -> {
                    println(makeTextGreen(client.getMenu()))
                }

                8 -> {
                    isLogged = false
                    userType = Type.None
                    client.exitAdmin()
                }

                -1 -> {
                    throw InterruptedException()
                }
            }
        } catch (ex: Exception) {
            println(makeTextGreen(ex.message.toString()))
        }
    }

    private fun fromStringToCorrectChoice(str: String, leftBorder: Int, rightBorder: Int): Int {
        val intValue = str.toInt()
        if (intValue !in leftBorder..rightBorder || intValue == -1) {
            throw IndexOutOfBoundsException("Неверный ввод.")
        }
        return intValue
    }


    private fun fromStringToIntWithOne(str: String): Int {
        return try {
            str.toInt()
        } catch (ex: Exception) {
            1
        }
    }

    private fun inputOrderList(): MutableMap<Int, Int> {
        val order = mutableMapOf<Int, Int>()
        println("Вводите ваш заказ в следующем формате: [ID_БЛЮДА] [КОЛ_ВО]. Окончанием ввода будет служить -1")
        var inp = readln()
        while (inp != "-1") {
            val splitted = inp.split(" ")
            if (splitted.size != 2) {
                throw IndexOutOfBoundsException("You must enter 2 numbers.")
            }
            val dishId = splitted[0].toInt()
            val count = splitted[1].toInt()

            order[dishId] = count
            inp = readln()
        }
        return order
    }

    private fun inputAccountData(): Pair<String, String> {
        print("Введите ваш логин: ")
        val login = readln()
        print("Введите ваш пароль: ")
        val password = readln()
        return Pair(login, password)
    }

    private fun makeTextGreen(str: String): String {
        return greenColor + str + resetColor
    }
}