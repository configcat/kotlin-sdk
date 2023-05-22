package com.configcat

import com.soywiz.klock.*

internal object DateTimeUtils {
    fun isValidDate(fetchTimeUnixSecond: Double): Boolean {
        try {
            DateTime(fetchTimeUnixSecond)
        } catch (e: DateException) {
            return false
        }
        return true
    }
}
