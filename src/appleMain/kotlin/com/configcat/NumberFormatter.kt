package com.configcat

import kotlinx.cinterop.UnsafeNumber
import platform.Foundation.NSNumber
import platform.Foundation.NSNumberFormatter
import kotlin.math.abs

@OptIn(UnsafeNumber::class)
internal actual fun doubleToString(doubleToString: Double): String {
    // Handle Double.NaN, Double.POSITIVE_INFINITY and Double.NEGATIVE_INFINITY
    if (doubleToString.isNaN() || doubleToString.isInfinite()) {
        return doubleToString.toString()
    }

    // To get similar result between different SDKs the Double value format is modified.
    // Between 1e-6 and 1e21 we don't use scientific-notation. Over these limits scientific-notation used but the
    // ExponentSeparator replaced with "e" and "e+".
    // "." used as decimal separator in all cases.
    val abs = abs(doubleToString)
    val formatter = NSNumberFormatter()
    formatter.usesGroupingSeparator = false
    formatter.minimumFractionDigits = 0u
    formatter.maximumFractionDigits = 17u
    if (1e-6 <= abs && abs < 1e21) {
        formatter.numberStyle = 1u
        return formatter.stringFromNumber(NSNumber(doubleToString)) ?: ""
    } else {
        val str = NSNumber(doubleToString)
        return str.description ?: ""
    }
}

@OptIn(UnsafeNumber::class)
internal actual fun formatDoubleForLog(doubleToFormat: Double): String {
    val formatter = NSNumberFormatter()
    formatter.usesGroupingSeparator = false
    formatter.minimumFractionDigits = 0u
    formatter.maximumFractionDigits = 4u
    formatter.numberStyle = 1u
    return formatter.stringFromNumber(NSNumber(doubleToFormat)) ?: ""
}
