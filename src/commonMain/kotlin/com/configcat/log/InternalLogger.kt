package com.configcat.log

import com.configcat.Hooks
import kotlin.time.Clock

internal class InternalLogger(
    private val logger: Logger,
    private val level: LogLevel,
    private val hooks: Hooks,
) {
    fun error(
        eventId: Int,
        message: String,
        throwable: Throwable? = null,
    ) {
        hooks.invokeOnError(message)
        if (isLevelAllowed(LogLevel.ERROR)) {
            logger.error("[$eventId] $message${throwable?.let { " ${it.message}" } ?: ""}")
        }
    }

    fun warning(
        eventId: Int,
        message: String,
    ) {
        if (isLevelAllowed(LogLevel.WARNING)) {
            logger.warning("[$eventId] $message")
        }
    }

    fun info(
        eventId: Int,
        message: String,
    ) {
        if (isLevelAllowed(LogLevel.INFO)) {
            logger.info("[$eventId] $message")
        }
    }

    fun debug(message: String) {
        if (isLevelAllowed(LogLevel.DEBUG)) {
            logger.debug("[0] $message")
        }
    }

    fun isLevelAllowed(requestedLevel: LogLevel): Boolean = requestedLevel >= level
}

internal class DefaultLogger : Logger {
    private val levelMap: HashMap<LogLevel, String> =
        hashMapOf(
            LogLevel.ERROR to "ERROR",
            LogLevel.WARNING to "WARNING",
            LogLevel.INFO to "INFO",
            LogLevel.DEBUG to "DEBUG",
        )

    override fun error(message: String) {
        printMessage(enrichMessage(message, LogLevel.ERROR))
    }

    override fun error(
        message: String,
        throwable: Throwable,
    ) {
        printMessage(enrichMessage("$message $throwable", LogLevel.ERROR))
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

    private fun enrichMessage(
        message: String,
        level: LogLevel,
    ): String = "${Clock.System.now()} [${levelMap[level]}]: ConfigCat - $message"
}
