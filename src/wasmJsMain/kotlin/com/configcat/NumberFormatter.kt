package com.configcat

internal actual fun doubleToString(doubleToString: Double): String = doubleToString.toString()

internal actual fun formatDoubleForLog(doubleToFormat: Double): String = commonFormatDoubleForLog(doubleToFormat)
