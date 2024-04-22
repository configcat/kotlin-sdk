package com.configcat

internal actual fun doubleToString(doubleToString: Double): String {
    return commonDoubleToString(doubleToString)
}

internal actual fun formatDoubleForLog(doubleToFormat: Double): String {
    return commonFormatDoubleForLog(doubleToFormat)
}
