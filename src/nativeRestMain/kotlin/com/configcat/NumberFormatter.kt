package com.configcat

internal actual fun doubleToString(doubleToString: Double): String = commonDoubleToString(doubleToString)

internal actual fun formatDoubleForLog(doubleToFormat: Double): String = commonFormatDoubleForLog(doubleToFormat)
