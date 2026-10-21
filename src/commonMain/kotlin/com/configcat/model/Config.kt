package com.configcat.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * ConfigCat config.
 */
@Serializable
public data class Config(
    /**
     * The config preferences.
     */
    @SerialName("p")
    val preferences: Preferences?,
    /**
     * Map of flags / settings.
     */
    @SerialName("f")
    var settings: Map<String, Setting>? = null,
    /**
     * List of segments.
     */
    @SerialName("s")
    var segments: Array<Segment>? = null,
) {
    internal fun isEmpty(): Boolean = this == empty

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Config) return false

        if (preferences != other.preferences) return false
        if (settings != other.settings) return false
        if (!segments.contentEquals(other.segments)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = preferences?.hashCode() ?: 0
        result = 31 * result + (settings?.hashCode() ?: 0)
        result = 31 * result + segments.contentHashCode()
        return result
    }

    internal companion object {
        val empty: Config = Config(null, mapOf(), arrayOf())
    }
}
