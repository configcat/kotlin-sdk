package com.configcat

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime


internal object DateTimeUtils {
    fun isValidDate(fetchTime: String): Boolean {
        fetchTime.toLongOrNull() ?: return false
        return true
    }

    fun Double.toDateTimeUTCString(): String {
        val instant = Instant.fromEpochSeconds(this.toLong())
        val dateTime = instant.toLocalDateTime(defaultTimeZone)
        return dateTime.toString() + "Z"
    }

    val defaultTimeZone = TimeZone.currentSystemDefault()
}
