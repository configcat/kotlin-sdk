package com.configcat.log

@Suppress("TooManyFunctions")
internal object ConfigCatLogMessages {
    /**
     * Log message for Config Service Cannot Initiate Http Calls warning. The log eventId 3200.
     */
    const val CONFIG_SERVICE_CANNOT_INITIATE_HTTP_CALLS_WARN =
        "Client is in offline mode, it cannot initiate HTTP calls."

    /**
     * Log message for Data Governance Is Out Of Sync warning. The log eventId 3002.
     */
    const val DATA_GOVERNANCE_IS_OUT_OF_SYNC_WARN =
        "The `builder.dataGovernance()` parameter specified at the client initialization is not in sync " +
            "with the preferences on the ConfigCat Dashboard. " +
            "Read more: https://configcat.com/docs/advanced/data-governance/"

    /**
     * Log message for Config Service Cache Write error. The log eventId is 2201.
     */
    const val CONFIG_SERVICE_CACHE_WRITE_ERROR = "Error occurred while writing the cache"

    /**
     * Log message for Config Service Cache Read error. The log eventId is 2200.
     */
    const val CONFIG_SERVICE_CACHE_READ_ERROR = "Error occurred while reading the cache."

    /**
     * Log message for Fetch Received 200 With Invalid Body error. The log eventId is 1105.
     */
    const val FETCH_RECEIVED_200_WITH_INVALID_BODY_ERROR =
        "Fetching config JSON was successful but the HTTP response content was invalid."

    /**
     * Log message for Fetch Failed Due To Redirect Loop error. The log eventId is 1104.
     */
    const val FETCH_FAILED_DUE_TO_REDIRECT_LOOP_ERROR =
        "Redirection loop encountered while trying to fetch config JSON. " +
            "Please contact us at https://configcat.com/support/"

    /**
     * Log message for Fetch Failed Due To Unexpected error. The log eventId is 1103.
     */
    const val FETCH_FAILED_DUE_TO_UNEXPECTED_ERROR = "Unexpected error occurred while trying to fetch config JSON."

    /**
     * Log message for Fetch Failed Due To Invalid Sdk Key error. The log eventId is 1100.
     */
    const val FETCH_FAILED_DUE_TO_INVALID_SDK_KEY_ERROR =
        "Your SDK Key seems to be wrong. You can find the valid SDK Key at https://app.configcat.com/sdkkey."

    /**
     * Log message for Config Json Is Not Presented errors when the method returns with default value.
     * The log eventId is 1000.
     *
     * @param key The feature flag key.
     * @param defaultParamName  The default parameter name.
     * @param defaultParamValue The default parameter value.
     * @return The formatted error message.
     */
    fun getConfigJsonIsNotPresentedWithDefaultValue(
        key: String,
        defaultParamName: String,
        defaultParamValue: Any?
    ): String {
        return "Config JSON is not present when evaluating setting '$key'. " +
            "Returning the `$defaultParamName` parameter that you specified in your " +
            "application: '$defaultParamValue'."
    }

    /**
     * Log message for Config Json Is Not Presented errors when the method returns with empty value.
     * The log eventId is 1000.
     *
     * @param emptyResult The empty result.
     * @return The formatted error message.
     */
    fun getConfigJsonIsNotPresentedWithEmptyResult(emptyResult: String): String {
        return "Config JSON is not present. Returning $emptyResult."
    }

    /**
     * Log message for Setting Evaluation Failed Due To Missing Key error. The log eventId is 1001.
     *
     * @param key               The feature flag key.
     * @param defaultParamName  The default parameter name.
     * @param defaultParamValue The default parameter value.
     * @param availableKeysSet  The set of available keys in the settings.
     * @return The formatted error message.
     */
    fun getSettingEvaluationFailedDueToMissingKey(
        key: String,
        defaultParamName: String,
        defaultParamValue: Any?,
        availableKeysSet: Set<String?>
    ): String {
        return "Failed to evaluate setting '$key' (the key was not found in config JSON). " +
            "Returning the `$defaultParamName` parameter that you specified in your " +
            "application: '$defaultParamValue'" + ". Available keys: [" + availableKeysSet.joinToString(
            ", ",
            transform = { availableKey -> "'$availableKey'" }
        ) + "]."
    }

    /**
     * Log message for Fetch Failed Due To Unexpected Http Response error. The log eventId is 1101.
     *
     * @param responseCode    The http response code.
     * @param responseMessage The http response message.
     * @return The formatted error message.
     */
    fun getFetchFailedDueToUnexpectedHttpResponse(responseCode: Int, responseMessage: String): String {
        return "Unexpected HTTP response was received while trying to fetch config JSON: $responseCode $responseMessage"
    }

    /**
     * Log message for Fetch Failed Due To Request Timeout error. The log eventId is 1102.
     *
     * @param connectTimeoutMillis Connect timeout in milliseconds.
     * @param readTimeoutMillis    Read timeout in milliseconds.
     * @param writeTimeoutMillis   Write timeout in milliseconds.
     * @return The formatted error message.
     */
    fun getFetchFailedDueToRequestTimeout(
        connectTimeoutMillis: Long,
        readTimeoutMillis: Long,
        writeTimeoutMillis: Long
    ): String {
        return "Request timed out while trying to fetch config JSON. " +
            "Timeout values: [connect: " + connectTimeoutMillis + "ms, " +
            "read: " + readTimeoutMillis + "ms, write: " + writeTimeoutMillis + "ms]"
    }

    /**
     * Log message for Setting For Variation Id Is Not Present error. The log eventId is 2011.
     *
     * @param variationId The variation id.
     * @return The formatted error message.
     */
    fun getSettingForVariationIdIsNotPresent(variationId: String): String {
        return "Could not find the setting for the specified variation ID: '$variationId'."
    }

    /**
     * Log message for Client Is Already Created warning. The log eventId 3000.
     *
     * @param sdkKey The ConfigCat client SDK key.
     * @return The formatted warn message.
     */
    fun getClientIsAlreadyCreated(sdkKey: String): String {
        return "There is an existing client instance for the specified SDK Key. No new client instance will be " +
            "created and the specified options callback is ignored. " +
            "Returning the existing client instance. SDK Key: '$sdkKey'."
    }

    /**
     * Log message for Targeting Is Not Possible warning. The log eventId 3001.
     *
     * @param key The feature flag setting key.
     * @return The formatted warn message.
     */
    fun getTargetingIsNotPossible(key: String): String {
        return "Cannot evaluate targeting rules and % options for setting '$key' (User Object is missing). " +
            "You should pass a User Object to the evaluation methods like `getValue()`/`getValueAsync()` " +
            "in order to make targeting work properly. Read more: https://configcat.com/docs/advanced/user-object/"
    }

    /**
     * Log message for Config Service Method Has No Effect Due To Closed Client warning. The log eventId 3201.
     *
     * @param methodName The method name.
     * @return The formatted warn message.
     */
    fun getConfigServiceMethodHasNoEffectDueToClosedClient(methodName: String): String {
        return "The client object is already closed, thus `$methodName` has no effect."
    }

    /**
     * Log message for Auto Poll Max Init Wait Time Reached warning. The log eventId 4200.
     *
     * @param maxInitWaitTimeSeconds The auto polling `maxInitWaitTimeSeconds` value.
     * @return The formatted warn message.
     */
    fun getAutoPollMaxInitWaitTimeReached(maxInitWaitTimeSeconds: Long): String {
        return "`maxInitWaitTimeSeconds` for the very first fetch reached (" + maxInitWaitTimeSeconds + "s)." +
            " Returning cached config."
    }

    /**
     * Log message for Config Service Status Changed info. The log eventId 5200.
     *
     * @param mode The change mode.
     * @return The formatted info message.
     */
    fun getConfigServiceStatusChanged(mode: String): String {
        return "Switched to $mode mode."
    }
}
