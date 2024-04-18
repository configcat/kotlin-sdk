package com.configcat

internal actual fun defaultCache(): ConfigCache {
    return EmptyConfigCache()
}
