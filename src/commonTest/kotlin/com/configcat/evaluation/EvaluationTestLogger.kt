package com.configcat.evaluation

import com.configcat.log.LogLevel
import com.configcat.log.Logger
class EvaluationTestLogger : Logger {
    private var logList: ArrayList<LogEvent> = arrayListOf()

    private val levelMap: HashMap<LogLevel, String> = hashMapOf(
        LogLevel.ERROR to "ERROR",
        LogLevel.WARNING to "WARNING",
        LogLevel.INFO to "INFO",
        LogLevel.DEBUG to "DEBUG"
    )

    override fun error(message: String) {
        logMessage(enrichMessage(message, LogLevel.ERROR), LogLevel.ERROR)
    }

    override fun error(message: String, throwable: Throwable) {
        logMessage(enrichMessage("$message $throwable", LogLevel.ERROR), LogLevel.ERROR)
    }

    override fun warning(message: String) {
        logMessage(enrichMessage(message, LogLevel.WARNING), LogLevel.WARNING)
    }

    override fun info(message: String) {
        logMessage(enrichMessage(message, LogLevel.INFO), LogLevel.INFO)
    }

    override fun debug(message: String) {
        logMessage(enrichMessage(message, LogLevel.DEBUG), LogLevel.DEBUG)
    }

    private fun logMessage(message: String, logLevel: LogLevel) {
        logList.add(LogEvent(logLevel, message))
    }

    fun getLogList(): List<LogEvent> {
        return logList
    }

    fun resetLogList() {
        logList = arrayListOf()
    }

    private fun enrichMessage(message: String, level: LogLevel): String {
        return "${levelMap[level]} $message"
    }
}

class LogEvent(val logLevel: LogLevel, val logMessage: String)
