package com.configcat.client.override

/**
 * Feature flag and setting overrides.
 */
public class FlagOverrides {
    /**
     * The override behaviour. It can be used to set preference on whether the local values should
     * override the remote values, or use local values only when a remote value doesn't exist,
     * or use it for local only mode.
     */
    public var behavior: OverrideBehavior = OverrideBehavior.LOCAL_ONLY

    /**
     * Describes the overrides' data source.
     */
    public var dataSource: OverrideDataSource = OverrideDataSource.map(mapOf())
}
