package restaurant

import java.io.FileWriter
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

internal object Logger {
    enum class Status {
        OK,
        ERROR
    }

    init {
        if (!Files.exists(Paths.get("./data"))) {
            Files.createDirectory(Paths.get("./data"))
        }
    }

    private var pathName = "data/logs.log"
    private var lock = Any()

    /**
     * Writes message to the log file.
     * Be careful, it is used not for status messages, only for comments
     * @param text string that system should log
     */
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

    /**
     * Writes status message to the log file.
     * @param text string that system should log
     * @param status status of the action
     */
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