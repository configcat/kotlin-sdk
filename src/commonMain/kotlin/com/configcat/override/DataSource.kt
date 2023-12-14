package com.configcat.override

import com.configcat.Setting

/**
 * Describes a data source for [FlagOverrides].
 */
public interface OverrideDataSource {
    /**
     * Gets all the overrides defined in the given source.
     */
    public fun getOverrides(): Map<String, Setting>

    /**
     * Companion object of [OverrideDataSource].
     */
    public companion object {
        /**
         * Create an [OverrideDataSource] that stores the overrides in a key-value map.
         */
        public fun map(map: Map<String, Any>): OverrideDataSource {
            return MapOverrideDataSource(map.map { it.key to Setting(it.value) }.toMap())
        }

        /**
         * Create an [OverrideDataSource] that stores the override settings in a key-value map.
         */
        public fun settings(map: Map<String, Setting>): OverrideDataSource {
            return SettingsOverrideDataSource(map)
        }
    }
}

internal class MapOverrideDataSource constructor(private val map: Map<String, Setting>) : OverrideDataSource {
    override fun getOverrides(): Map<String, Setting> {
        return map
    }
}
internal class SettingsOverrideDataSource constructor(private val map: Map<String, Setting>) : OverrideDataSource {
    override fun getOverrides(): Map<String, Setting> {
        return map
    }
}
