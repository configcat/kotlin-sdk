package com.configcat

import com.soywiz.klock.*

internal object DateTimeUtils {

    /**
     * HTTP Date header formatter. Date: day-name, day month year hour:minute:second GMT
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Date">mdn docs</a>
     */
    private val dateFormat: DateFormat = DateFormat("EEE, dd MMM yyyy HH:mm:ss z") // Construct a new DateFormat from a String

    fun isValidDate(dateString: String): Boolean {
        try {
            dateFormat.parse(dateString)
        } catch (e: DateException) {
            return false
        }
        return true
    }

    fun parse(dateTime: String): DateTime {
        return dateFormat.parse(dateTime).utc
    }

    fun format(dateTime: DateTime): String {
        return dateFormat.format(dateTime)
    }
}
