package com.configcat

internal actual fun doubleToString(doubleToString: Double): String {
    // The custom double format rules based on the JS double format. Simple toString call is enough.
    return doubleToString.toString()
}

internal actual fun formatDoubleForLog(doubleToFormat: Double): String = commonFormatDoubleForLog(doubleToFormat)
