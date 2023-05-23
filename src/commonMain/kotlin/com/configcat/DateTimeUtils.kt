package com.configcat

import com.soywiz.klock.*

internal object DateTimeUtils {
    fun isValidDate(fetchTimeUnixMillis: Double): Boolean {
        try {
            DateTime(fetchTimeUnixMillis)
        } catch (e: DateException) {
            return false
        }
        return true
    }
}
