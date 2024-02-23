package restaurant.usersystem

open class User(val login: String, internal val password: String, var role: UserRole) {
    var isLoggedNow : Boolean = false

}