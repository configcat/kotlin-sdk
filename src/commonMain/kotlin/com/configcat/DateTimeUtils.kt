package com.configcat

import com.soywiz.klock.*

internal object DateTimeUtils {
    fun isValidDate(fetchTime: String): Boolean {
        try {
            val fetchTimeSeconds = fetchTime.toDouble()
            DateTime(fetchTimeSeconds * 1000)
        } catch (e: Exception) {
            return false
        }
        return true
    }
}
