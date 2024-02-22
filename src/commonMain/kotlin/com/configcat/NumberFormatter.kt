package com.configcat

import kotlin.math.absoluteValue

public interface NumberFormatter {
    /**
     * Convert [Double] to [String] based on the following format rules.
     *
     * To get similar result between different SDKs the Double value format is modified.
     * Between 1e-6 and 1e21 we don't use scientific-notation. Over these limits scientific-notation used but the ExponentSeparator replaced with "e" and "e+".
     * "." used as decimal separator in all cases.
     *
     * For [Double.NaN], [Double.POSITIVE_INFINITY] and [Double.NEGATIVE_INFINITY] simple String representation used.
     */
    public fun doubleToString(doubleToString: Double): String
}

// TODO DefaultNumberFormatter can be removed if never used
internal class DefaultNumberFormatter : NumberFormatter {
    override fun doubleToString(doubleToString: Double): String {
        if (doubleToString.isNaN() || doubleToString.isInfinite()) {
            return doubleToString.toString()
        }
        // Scientific Notation use cannot be turned on or off in native and no formatter can be used properly.
        // As best effort we replace the "," and the "E" if presented.
        val stringFormatScientificNotation = doubleToString.toString().replace(",", ".")
        return if (doubleToString.absoluteValue > 1) {
            stringFormatScientificNotation.replace("E", "e+")
        } else {
            stringFormatScientificNotation.replace("E-", "e-")
        }
    }
}

internal expect fun numberFormatter(): NumberFormatter
