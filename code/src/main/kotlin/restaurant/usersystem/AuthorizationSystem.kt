package restaurant.usersystem

import restaurant.Logger
import restaurant.order.OrderSystem

class AuthorizationSystem(private var system : OrderSystem) {
    private val users = mutableSetOf<User>()
    private var userIncrement = 0

    private val getUserId : Int
        get() {
            userIncrement += 1
            return userIncrement
        }

    fun addUserToSystem(login: String, encryptedPassw: String, role: UserRole): Boolean {
        // If this user already exists...
        val resultOfSearch = users.find { it.compareData(login, encryptedPassw) }
        if (resultOfSearch != null) {
            Logger.writeToLog("Attempt for register a new user with login $login. ERROR")
            return false
        }

        if (role == UserRole.Visitor) {
            users.add(Visitor(getUserId, login, encryptedPassw, system))
        } else if (role == UserRole.Admin) {
            users.add(Admin(getUserId, login, encryptedPassw, system))
        }
        Logger.writeToLog("Attempt for register a new user with login $login. OK")
        return true
    }

    fun tryAuth(login: String, encryptedPassw: String): User? {
        val result = users.find { it.compareData(login, encryptedPassw) }
        if (result != null) {
            result.isLoggedNow = true
            Logger.writeToLog("Attempt for auth with login $login. Result: OK")
        } else {
            Logger.writeToLog("Attempt for auth with login $login. Result: ERROR")
        }
        return result
    }

    fun exitFromSystem(user: User?) {
        if (user == null) {
            Logger.writeToLog("Attempt for exit with login NULL. Result: ERROR")
            return
        }
        Logger.writeToLog("Attempt for exit with id ${user.id}. Result: OK")
        user.isLoggedNow = false
    }
}