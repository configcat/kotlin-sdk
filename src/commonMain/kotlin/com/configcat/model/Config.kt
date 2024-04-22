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

    internal companion object {
        val empty: Config = Config(null, mapOf(), arrayOf())
    }
}
