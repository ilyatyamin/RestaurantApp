package restaurant

import java.io.File
import java.util.*

object Logger {
    private var pathName = "logs.log"
    private var file = File(pathName)

    fun changePathName(path : String) {
        pathName = path
    }

    fun writeToLog(text : String) {
        try {
            file.writeText("${Date()}. $text", Charsets.UTF_8)
        } catch (ex : Exception) {
            file = File(pathName)
            file.writeText("There are an error in writing to file. ${ex.message}", Charsets.UTF_8)
            file.writeText("${Date()}. $text", Charsets.UTF_8)
        }
    }
}