package com.configcat

import kotlin.math.abs

public class IosNumberFormatter : NumberFormatter {
    override fun doubleToString(doubleToString: Double): String {
        return doubleToString.toString()
        // Handle Double.NaN, Double.POSITIVE_INFINITY and Double.NEGATIVE_INFINITY
//        if (doubleToString.isNaN() || doubleToString.isInfinite()) {
//            return doubleToString.toString()
//        }

        // To get similar result between different SDKs the Double value format is modified.
        // Between 1e-6 and 1e21 we don't use scientific-notation. Over these limits scientific-notation used but the
        // ExponentSeparator replaced with "e" and "e+".
        // "." used as decimal separator in all cases.
//        val abs = abs(doubleToString)
//        val formatter = NSNumberFormatter()
//        formatter.minimumFractionDigits = 0u
//        formatter.maximumFractionDigits = 2u
//        formatter.numberStyle = 1u //Decimal
//        return formatter.stringFromNumber(NSNumber(double))!!
//        val fmt =
//            if (1e-6 <= abs && abs < 1e21) DecimalFormat("#.#################") else DecimalFormat("#.#################E0")
//        val symbols = DecimalFormatSymbols.getInstance(Locale.UK)
//        if (abs > 1) {
//            symbols.exponentSeparator = "e+"
//        } else {
//            symbols.exponentSeparator = "e"
//        }
//        fmt.decimalFormatSymbols = symbols
//        return fmt.format(doubleToString)
    }
}

internal actual fun numberFormatter(): NumberFormatter {
    return IosNumberFormatter()
}