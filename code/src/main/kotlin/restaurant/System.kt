package restaurant

import restaurant.order.OrderSystem
import restaurant.usersystem.*

data class StatusOfToken(val id: String, val isExpired: Boolean, val role: String)

/**
 * The main control center of restaurant
 * @constructor Create restaurant with systems
 */
class System {
    init {
        Logger.writeToLog("-".repeat(30))
        Logger.writeToLog("System was created.")
    }

    private val orderSystem = OrderSystem()
    private val authSystem = AuthorizationSystem(orderSystem)

    /**
     * Method for private goals. This method called by tryAuthVisitor() and tryAuthAdmin()
     * @return abstract object User. Despite this, it can be cast to Admin or to Visitor.
     */
    private fun tryAuth(login: String, password: String): User? {
        return authSystem.tryAuth(login, Encryptor.encryptThis(password))
    }

    /**
     * Method for visitor authentication
     * @param login visitor's login
     * @param password visitor's password
     * @return Visitor object
     * @throws SecurityException if this account didn't save in database, and other cases
     */
    fun tryAuthVisitor(login: String, password: String): Visitor {
        val possibleUser = tryAuth(login, password)
        if (possibleUser != null && possibleUser.role == UserRole.Visitor) {
            return possibleUser as Visitor
        }
        if (possibleUser == null) {
            throw SecurityException("No account has been registered with this data")
        }
        throw SecurityException("Error in data.")
    }

    /**
     * Method for admin authentication
     * @param login admins login
     * @param password admins password
     * @return Admin object
     * @throws SecurityException if this account didn't save in database, and other cases
     */
    fun tryAuthAdmin(login: String, password: String): Admin {
        val possibleUser = tryAuth(login, password)
        if (possibleUser != null && possibleUser.role == UserRole.Admin) {
            return possibleUser as Admin
        }
        if (possibleUser == null) {
            throw SecurityException("No account has been registered with this data")
        }
        throw SecurityException("Error in data.")
    }

    /**
     * Method for user (it can be Visitor or Admin) registration
     * @param login String that be the user's login
     * @param password String that be the user's password
     * @param role User's role: enum object
     * @return boolean variable: is it successful to register new user?
     * @throws SecurityException if login or password is empty
     */
    fun registerNewUser(login: String, password: String, role: UserRole): Boolean {
        if (login.isEmpty() || password.isEmpty()) {
            throw SecurityException("Data cannot be null")
        }
        return authSystem.addUserToSystem(login, Encryptor.encryptThis(password), role)
    }

    /**
     * Return's menu string-presentation
     * @return menu in the string format
     */
    fun getMenu(): String {
        return orderSystem.menuObj.getString()
    }

    /**
     * Exit user's account
     * @param user object of User
     */
    fun exitUser(user: User?) {
        return authSystem.exitFromSystem(user)
    }

}