package restaurant.usersystem

import restaurant.Logger

class AuthorizationSystem {
    private val users = mutableSetOf<User>()

    fun addUserToSystem(login: String, encryptedPassw: String, role: UserRole): Boolean {
        // If this user already exists...
        val resultOfSearch = users.find { it.login == login && it.password == encryptedPassw }
        if (resultOfSearch == null || (resultOfSearch.isLoggedNow)) {
            Logger.writeToLog("Attempt for register a new user with login $login. ERROR")
            return false
        }

        if (role == UserRole.Visitor) {
            users.add(Visitor(login, encryptedPassw))
        } else if (role == UserRole.Admin) {
            users.add(Admin(login, encryptedPassw))
        }
        Logger.writeToLog("Attempt for register a new user with login $login. OK")
        return true
    }

    fun tryAuth(login: String, encryptedPassw: String): User? {
        val result = users.find { it.login == login && it.password == encryptedPassw }
        if (result != null) {
            result.isLoggedNow = true;
        }
        Logger.writeToLog("Attempt for auth with login $login. Result: $result")
        return result
    }

    fun exitFromSystem(user : User) {
        Logger.writeToLog("Attempt for auth with login ${user.login}. Result: OK")
        user.isLoggedNow = false;
    }
}