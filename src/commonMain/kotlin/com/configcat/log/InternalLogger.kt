package com.configcat.log

import com.configcat.Hooks
import com.soywiz.klock.DateTime

internal class InternalLogger(private val logger: Logger, private val level: LogLevel, private val hooks: Hooks) {
    fun error(message: String) {
        hooks.invokeOnError(message)
        if (shouldLog(LogLevel.ERROR)) {
            logger.error(message)
        }
    }

    fun warning(message: String) {
        if (shouldLog(LogLevel.WARNING)) {
            logger.warning(message)
        }
    }

    fun info(message: String) {
        if (shouldLog(LogLevel.INFO)) {
            logger.info(message)
        }
    }

    fun debug(message: String) {
        if (shouldLog(LogLevel.DEBUG)) {
            logger.debug(message)
        }
    }

    private fun shouldLog(requestedLevel: LogLevel): Boolean {
        return requestedLevel >= level
    }
}

internal class DefaultLogger : Logger {
    private val levelMap: HashMap<LogLevel, String> = hashMapOf(
        LogLevel.ERROR to "ERROR",
        LogLevel.WARNING to "WARNING",
        LogLevel.INFO to "INFO",
        LogLevel.DEBUG to "DEBUG",
    )

    override fun error(message: String) {
        printMessage(enrichMessage(message, LogLevel.ERROR))
    }

    override fun warning(message: String) {
        printMessage(enrichMessage(message, LogLevel.WARNING))
    }

    override fun info(message: String) {
        printMessage(enrichMessage(message, LogLevel.INFO))
    }

    override fun debug(message: String) {
        printMessage(enrichMessage(message, LogLevel.DEBUG))
    }

    private fun printMessage(message: String) {
        println(message)
    }

    private fun enrichMessage(message: String, level: LogLevel): String {
        return "${DateTime.now().toString("yyyy-MM-dd HH:mm:ss z")} [${levelMap[level]}]: ConfigCat - $message"
    }
}
