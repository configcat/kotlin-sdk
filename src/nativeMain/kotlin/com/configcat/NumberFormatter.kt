package com.configcat

internal actual fun numberFormatter(): NumberFormatter {
    // TODO is this valid? what is based on the native?
   return DefaultNumberFormatter()
}