package com.configcat

import kotlin.time.Instant

internal object DateTimeUtils {
    fun isValidDate(fetchTime: String): Boolean {
        fetchTime.toLongOrNull() ?: return false
        return true
    }

    fun Double.toDateTimeUTCString(): String {
        val dateInMillisecond: Long = this.toLong() * 1000
        val instant = Instant.fromEpochMilliseconds(dateInMillisecond)
        return instant.toString()
    }
}
