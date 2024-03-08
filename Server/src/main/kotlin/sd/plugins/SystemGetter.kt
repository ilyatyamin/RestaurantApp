package sd.plugins

import restaurant.System

/**
 * Provide one logical access point to the system object for the routing Ktor service.
 */
class SystemGetter {
    companion object {
        private val systemObj = System()

        val system: System
            get() = systemObj
    }
}