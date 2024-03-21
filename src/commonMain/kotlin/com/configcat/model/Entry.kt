package com.configcat.model

import com.configcat.Constants
import com.configcat.DateTimeUtils
import com.configcat.parseConfigJson
import com.soywiz.klock.DateTime

internal data class Entry(
    val config: Config,
    val eTag: String,
    val configJson: String,
    val fetchTime: DateTime,
) {
    fun isEmpty(): Boolean = this === empty

    companion object {
        val empty: Entry = Entry(Config.empty, "", "", Constants.distantPast)

        fun fromString(cacheValue: String?): Entry {
            if (cacheValue.isNullOrEmpty()) {
                return empty
            }
            val fetchTimeIndex = cacheValue.indexOf("\n")
            val eTagIndex = cacheValue.indexOf("\n", fetchTimeIndex + 1)
            require(fetchTimeIndex > 0 && eTagIndex > 0) { "Number of values is fewer than expected." }
            val fetchTimeRaw = cacheValue.substring(0, fetchTimeIndex)
            require(DateTimeUtils.isValidDate(fetchTimeRaw)) { "Invalid fetch time: $fetchTimeRaw" }
            val fetchTimeUnixMillis = fetchTimeRaw.toLong()
            val eTag = cacheValue.substring(fetchTimeIndex + 1, eTagIndex)
            require(eTag.isNotEmpty()) { "Empty eTag value." }
            val configJson = cacheValue.substring(eTagIndex + 1)
            require(configJson.isNotEmpty()) { "Empty config jsom value." }
            return try {
                val config: Config = parseConfigJson(configJson)
                Entry(config, eTag, configJson, DateTime(fetchTimeUnixMillis))
            } catch (e: Exception) {
                throw IllegalArgumentException("Invalid config JSON content: $configJson", e)
            }
        }
    }

    fun serialize(): String {
        return "${fetchTime.unixMillis.toLong()}\n${eTag}\n$configJson"
    }
}
