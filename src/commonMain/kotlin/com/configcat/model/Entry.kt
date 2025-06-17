package com.configcat.model

import com.configcat.Constants
import com.configcat.DateTimeUtils
import com.configcat.DateTimeUtils.defaultTimeZone
import com.configcat.Helpers
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

internal data class Entry(
    val config: Config,
    val eTag: String,
    val configJson: String,
    val fetchTime: LocalDateTime,
) {
    var cacheString: String = ""
        private set

    init {
        cacheString = serialize(fetchTime, eTag, configJson)
    }

    fun isEmpty(): Boolean = this === empty

    fun isExpired(threshold: LocalDateTime): Boolean {
        return fetchTime <= threshold
    }

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
                val config: Config = Helpers.parseConfigJson(configJson)
                Entry(
                    config,
                    eTag,
                    configJson,
                    Instant.fromEpochMilliseconds(fetchTimeUnixMillis)
                        .toLocalDateTime(defaultTimeZone)
                )
            } catch (e: Exception) {
                throw IllegalArgumentException("Invalid config JSON content: $configJson", e)
            }
        }
    }

    private fun serialize(
        fetchTime: LocalDateTime,
        eTag: String,
        configJson: String,
    ): String {
        return "${fetchTime.toInstant(defaultTimeZone).toEpochMilliseconds()}\n${eTag}\n$configJson"
    }
}
