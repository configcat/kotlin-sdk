package com.configcat

import korlibs.time.DateTime


internal object DateTimeUtils {
    fun isValidDate(fetchTime: String): Boolean {
        fetchTime.toLongOrNull() ?: return false
        return true
    }

    fun Double.toDateTimeUTCString(): String {
        val dateInMillisecond: Long = this.toLong() * 1000
        val dateTime = DateTime.fromUnixMillis(dateInMillisecond)
        return dateTime.toString("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    }
}
