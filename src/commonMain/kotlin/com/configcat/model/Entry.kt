package com.configcat.model

import com.configcat.isValidDate
import com.configcat.parseConfigJson
import kotlin.time.Instant

internal data class Entry(
    val config: Config,
    val eTag: String,
    val configJson: String,
    val fetchTime: Instant,
) {
    var cacheString: String = ""
        private set

    init {
        cacheString = serialize(fetchTime, eTag, configJson)
    }

    fun isEmpty(): Boolean = this === empty

    fun isExpired(threshold: Instant): Boolean = fetchTime <= threshold

    companion object {
        val empty: Entry = Entry(Config.empty, "", "", Instant.DISTANT_PAST)

        fun fromString(cacheValue: String?): Entry {
            if (cacheValue.isNullOrEmpty()) {
                return empty
            }
            val fetchTimeIndex = cacheValue.indexOf("\n")
            val eTagIndex = cacheValue.indexOf("\n", fetchTimeIndex + 1)
            require(fetchTimeIndex > 0 && eTagIndex > 0) { "Number of values is fewer than expected." }
            val fetchTimeRaw = cacheValue.take(fetchTimeIndex)
            require(fetchTimeRaw.isValidDate()) { "Invalid fetch time: $fetchTimeRaw" }
            val fetchTimeUnixMillis = fetchTimeRaw.toLong()
            val eTag = cacheValue.substring(fetchTimeIndex + 1, eTagIndex)
            require(eTag.isNotEmpty()) { "Empty eTag value." }
            val configJson = cacheValue.substring(eTagIndex + 1)
            require(configJson.isNotEmpty()) { "Empty config jsom value." }
            return try {
                val config: Config = configJson.parseConfigJson()
                Entry(config, eTag, configJson, Instant.fromEpochMilliseconds(fetchTimeUnixMillis))
            } catch (e: Exception) {
                throw IllegalArgumentException("Invalid config JSON content: $configJson", e)
            }
        }
    }

    private fun serialize(
        fetchTime: Instant,
        eTag: String,
        configJson: String,
    ): String = "${fetchTime.toEpochMilliseconds()}\n${eTag}\n$configJson"
}
