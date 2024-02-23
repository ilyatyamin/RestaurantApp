package restaurant

import restaurant.usersystem.AuthorizationSystem
import restaurant.usersystem.User
import restaurant.usersystem.UserRole
import restaurant.usersystem.Visitor
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class System {
    /** Объект, позволяющий шифровать сообщения и расшифровывать их */
    private object Encryptor {
        private var password = "ASDFGHEYVN47CB4F"

        /** Производит шифрование строки согласно протоколу AES
         * @return зашифрованную строку **/
        @OptIn(ExperimentalEncodingApi::class)
        fun encryptThis(input: String): String {
            // Создаем объект шифра
            val cipher = Cipher.getInstance("AES")

            // Создает уникальный ключ и инициализируем шифр
            val keySpec = SecretKeySpec(password.toByteArray(), "AES")
            cipher.init(Cipher.ENCRYPT_MODE, keySpec)

            // Шифруем и возвращаем зашифрованное
            val encrypt = cipher.doFinal(input.toByteArray())
            return Base64.encode(encrypt)
        }

        /** Производит расшифрование строки согласно протоколу AES
         * @return расшифрованную строку **/
        @OptIn(ExperimentalEncodingApi::class)
        fun decrypt(input: String): String {
            // Создаем объект шифра
            val cipher = Cipher.getInstance("AES")

            // Создает уникальный ключ и инициализируем шифр
            val keySpec = SecretKeySpec(password.toByteArray(), "AES")
            cipher.init(Cipher.DECRYPT_MODE, keySpec)

            // Расшифруем и возвращаем
            val encrypt = cipher.doFinal(Base64.decode(input))
            return String(encrypt)
        }
    }

    val currentOrders = mutableListOf<Order>()
    var menu = Menu()
    val authSystem = AuthorizationSystem()


    fun tryAuth(login : String, password : String) : User? {
        return authSystem.tryAuth(login, Encryptor.encryptThis(password))
    }

    fun registerNewUser(login : String, password : String, role : UserRole) : Boolean {
        return authSystem.addUserToSystem(login, Encryptor.encryptThis(password), role)
    }

    fun exitUser(user : User) {
        return authSystem.exitFromSystem(user)
    }

    private fun saveState() {}
}