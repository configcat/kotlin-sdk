package com.configcat

import platform.Foundation.NSNumber
import platform.Foundation.NSNumberFormatter
import platform.Foundation.NSNumberFormatterStyle
import kotlin.math.abs

internal actual fun doubleToString(doubleToString: Double): String {
    // TODO remove commonDoubleToString if not used any more
    // return commonDoubleToString(doubleToString)

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
    formatter.minimumFractionDigits = 0u
    formatter.maximumFractionDigits = 17u
    if (1e-6 <= abs && abs < 1e21) {
        formatter.numberStyle = NSNumberFormatterStyle.NSNumberFormatterDecimalStyle
    } else {
        formatter.exponentSymbol = "e"
        formatter.numberStyle = NSNumberFormatterStyle.NSNumberFormatterScientificStyle
    }
    return formatter.stringFromNumber(NSNumber(doubleToString))

}

internal actual fun formatDoubleForLog(doubleToFormat: Double): String {
    // TODO remove commonFormatDoubleForLog if not used any more
    // return commonFormatDoubleForLog(doubleToFormat)
    val formatter = NSNumberFormatter()
    formatter.minimumFractionDigits = 0u
    formatter.maximumFractionDigits = 4u
    formatter.numberStyle = NSNumberFormatterStyle.NSNumberFormatterDecimalStyle
    return formatter.stringFromNumber(NSNumber(doubleToFormat))
}
