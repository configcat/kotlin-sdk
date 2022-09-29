package com.configcat.fetch

import com.configcat.ConfigCatClient

/**
 * Represents the result of a [ConfigCatClient.forceRefresh].
 */
public class RefreshResult(public val isSuccess: Boolean, public val error: String?)