package com.configcat

public class JsNumberFormatter : NumberFormatter {
    override fun doubleToString(doubleToString: Double): String {
        // The custom double format rules based on the JS double format. Simple toString call is enough.
        return doubleToString.toString()
    }
}

internal actual fun numberFormatter(): NumberFormatter {
    return JsNumberFormatter()
}
