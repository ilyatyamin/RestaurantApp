package restaurant

import java.io.FileWriter
import java.util.*

object Logger {
    enum class Status {
        OK,
        ERROR,
        NEUTRAL
    }

    private var pathName = "logs.log"
    private var lock = Any()

    fun changePathName(path: String) {
        pathName = path
    }

    fun writeToLog(text: String) {
        try {
            synchronized(lock) {
                FileWriter(pathName, true).use {
                    it.write("[${Date()}]. $text. \n")
                }
            }
        } catch (_: Exception) {
        }
    }

    fun writeToLogResult(text: String, status : Status) {
        try {
            synchronized(lock) {
                FileWriter(pathName, true).use {
                    it.write("[${Date()}]. $text. Status: $status \n")
                }
            }
        } catch (_: Exception) {
        }
    }
}