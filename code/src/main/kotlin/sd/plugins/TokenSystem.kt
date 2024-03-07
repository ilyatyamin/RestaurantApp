package sd.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import restaurant.Encryptor
import restaurant.StatusOfToken
import restaurant.usersystem.Admin
import restaurant.usersystem.User
import restaurant.usersystem.UserRole
import restaurant.usersystem.Visitor
import java.util.*

/**
 * Object that creates and managing token's of users
 * In two words, when user logins to the account, a system creates a special token
 * that will claim in 1 hour.
 * After 1 hour, user must log in one more time.
 * Token is the special encrypted key, and all actions in the restaurant system are executed with this token
 */
object TokenSystem {
    private const val SECRET = "FYG3YYEG7QDWJXgjdsg3tfy243Y4Dd"
    private const val ISSUER = "http://0.0.0.0:8080/"
    private val DEPENDENCIES = mutableMapOf<String, User>()

    /**
     * Create secret token
     *
     * @param userId userId
     * @param user user object
     * @return secret token key
     */
    fun createToken(userId: String, user: User): String {
        when (user.role) {
            UserRole.Visitor -> {
                val key = JWT.create()
                    .withAudience(UserRole.Visitor.toString())
                    .withIssuer(ISSUER)
                    .withClaim("username", userId.toInt())
                    .withExpiresAt(Date(System.currentTimeMillis() + 6000000))
                    .sign(Algorithm.HMAC512(SECRET))

                DEPENDENCIES[key] = user
                return key
            }

            UserRole.Admin -> {
                val key = JWT.create()
                    .withAudience(UserRole.Admin.toString())
                    .withIssuer(ISSUER)
                    .withClaim("username", userId.toInt())
                    .withExpiresAt(Date(System.currentTimeMillis() + 6000000))
                    .sign(Algorithm.HMAC512(SECRET))

                DEPENDENCIES[key] = user
                return key
            }
        }

    }

    /**
     * Decodes the token and makes a StatusOfToken structure
     * @param token token string
     * @return StatusOfToken that contains userId, boolean variable isExpired, and audience (userRole)
     */
    private fun decodeToken(token: String): StatusOfToken {
        val decoder = JWT.decode(token)
        return StatusOfToken(
            Encryptor.encryptThis(decoder.claims["username"].toString()),
            decoder.expiresAt <= Date(),
            decoder.audience.first()
        )
    }

    /**
     * Validate the token, check the expiry date, user's role
     * and return's Admin object if all data is correct
     * @param token token string
     * @return Admin object if all data is right
     * @throws IllegalAccessException if token has problems
     */
    internal fun getAdminByToken(token: String): Admin {
        val status = decodeToken(token)
        if (status.isExpired) {
            throw IllegalAccessException("Token is expired.")
        }
        if (status.role != "Admin") {
            throw IllegalAccessException("You are not admin.")
        }
        if (!DEPENDENCIES.containsKey(token)) {
            throw IllegalAccessException("Your token is not valid.")
        }
        return DEPENDENCIES[token] as Admin
    }

    /**
     * Validate the token, check the expiry date, user's role
     * and return's Visitor object if all data is correct
     * @param token token string
     * @return Visitor object if all data is right
     * @throws IllegalAccessException if token has problems
    */
    internal fun getVisitorByToken(token: String): Visitor {
        val status = decodeToken(token)
        if (status.isExpired) {
            throw IllegalAccessException("Token is expired.")
        }
        if (status.role != "Visitor") {
            throw IllegalAccessException("You are not visitor.")
        }
        if (!DEPENDENCIES.containsKey(token)) {
            throw IllegalAccessException("Your token is not valid.")
        }
        return DEPENDENCIES[token] as Visitor
    }
}