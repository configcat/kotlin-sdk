package com.configcat

internal object DateTimeUtils {
    fun isValidDate(fetchTime: String): Boolean {
        fetchTime.toLongOrNull() ?: return false
        return true
    }
}
