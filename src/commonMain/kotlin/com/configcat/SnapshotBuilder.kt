package com.configcat

import com.configcat.log.InternalLogger
import com.configcat.override.FlagOverrides
import com.configcat.override.OverrideBehavior
import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic

internal class SnapshotBuilder(
    private val flagEvaluator: FlagEvaluator,
    private val flagOverrides: FlagOverrides?,
    private val logger: InternalLogger,
    defaultUser: ConfigCatUser?,
) {
    public val defaultUser: AtomicRef<ConfigCatUser?> = atomic(defaultUser)

    public fun buildSnapshot(inMemoryResult: InMemoryResult): ConfigCatClientSnapshot {
        val inMemorySettings = calcInMemorySettingsWithOverrides(inMemoryResult)
        return Snapshot(
            flagEvaluator,
            inMemorySettings.settingResult,
            inMemorySettings.cacheState,
            defaultUser.value,
            logger,
        )
    }

    private fun calcInMemorySettingsWithOverrides(inMemoryResult: InMemoryResult): InMemoryResult {
        if (flagOverrides != null) {
            return when (flagOverrides.behavior) {
                OverrideBehavior.LOCAL_ONLY ->
                    InMemoryResult(
                        SettingResult(
                            flagOverrides.dataSource.getOverrides(),
                            Constants.distantPast,
                        ),
                        ClientCacheState.HAS_LOCAL_OVERRIDE_FLAG_DATA_ONLY,
                    )

                OverrideBehavior.LOCAL_OVER_REMOTE -> {
                    val remote = inMemoryResult.settingResult.settings
                    val local = flagOverrides.dataSource.getOverrides()
                    InMemoryResult(
                        SettingResult(remote + local, inMemoryResult.settingResult.fetchTime),
                        inMemoryResult.cacheState,
                    )
                }

                OverrideBehavior.REMOTE_OVER_LOCAL -> {
                    val remote = inMemoryResult.settingResult.settings
                    val local = flagOverrides.dataSource.getOverrides()
                    InMemoryResult(
                        SettingResult(local + remote, inMemoryResult.settingResult.fetchTime),
                        inMemoryResult.cacheState,
                    )
                }
            }
        }
        return inMemoryResult
    }
}
