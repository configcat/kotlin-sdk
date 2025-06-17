package com.configcat.log

import com.configcat.DateTimeUtils.defaultTimeZone
import com.configcat.Hooks
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.toLocalDateTime

internal class InternalLogger(private val logger: Logger, val level: LogLevel, private val hooks: Hooks) {
    fun error(
        eventId: Int,
        message: String,
        throwable: Throwable? = null,
    ) {
        hooks.invokeOnError(message)
        if (shouldLog(LogLevel.ERROR)) {
            logger.error("[$eventId] $message${throwable?.let { " ${it.message}" } ?: ""}")
        }
    }

    fun warning(
        eventId: Int,
        message: String,
    ) {
        if (shouldLog(LogLevel.WARNING)) {
            logger.warning("[$eventId] $message")
        }
    }

    fun info(
        eventId: Int,
        message: String,
    ) {
        if (shouldLog(LogLevel.INFO)) {
            logger.info("[$eventId] $message")
        }
    }

    fun debug(message: String) {
        if (shouldLog(LogLevel.DEBUG)) {
            logger.debug("[0] $message")
        }
    }

    private fun shouldLog(requestedLevel: LogLevel): Boolean {
        return requestedLevel >= level
    }
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
    ): String {

        val now = Clock.System.now().toLocalDateTime(defaultTimeZone)

        @OptIn(FormatStringsInDatetimeFormats::class)
        val format = LocalDateTime.Format {
            byUnicodePattern("yyyy-MM-dd HH:mm:ss")
        }

        val formatted = format.format(now) + defaultTimeZone.id

        return "$formatted [${levelMap[level]}]: ConfigCat - $message"
    }
}
