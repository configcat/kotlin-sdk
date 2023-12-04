package com.configcat.override

import com.configcat.model.Setting

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
            return MapOverrideDataSource(map.map { it.key to convertToSetting(it.value) }.toMap())
        }

        private fun convertToSetting(value: Any): Setting {
            val setting = Setting()
            //TODO remove prtln
            if(value is Int) {
                println("this is int $value")
                println()
            }
            if(value is Number) {
                println("this is double $value")
                println()
            }
            when (value) {
                is Boolean -> {
                    println("Boolean")
                    setting.settingsValue.booleanValue = value
                    setting.type = 0
                }

                is Int -> {
                    println("INT")

                    setting.settingsValue.integerValue = value
                    setting.type = 2
                }

                is Double -> {
                    println("DOUBLE")

                    setting.settingsValue.doubleValue = value
                    setting.type = 3
                }

                else -> {
                    println("STRING")

                    setting.settingsValue.stringValue = value.toString()
                    setting.type = 1
                }
            }
            return setting
        }
    }
}

internal class MapOverrideDataSource constructor(private val map: Map<String, Setting>) : OverrideDataSource {
    override fun getOverrides(): Map<String, Setting> {
        return map
    }
}
