package com.configcat

/**
 * Events fired by [ConfigCatClient].
 */
public class Hooks {
    /**
     * This event is sent when the SDK reaches the ready state.
     * If the SDK is configured with lazy load or manual polling it's considered ready right after instantiation.
     * If it's using auto polling, the ready state is reached when the SDK has a valid config.json loaded into
     * memory either from cache or from HTTP. If the config couldn't be loaded neither from cache nor
     * from HTTP the `onClientReady` event fires when the auto polling's `maxInitWaitTimeInSeconds` is reached.
     */
    public val onReady: MutableList<() -> Unit> = mutableListOf()

    /**
     * This event is sent when the SDK loads a valid config.json into memory from cache,
     * and each subsequent time when the loaded config.json changes via HTTP.
     */
    public val onConfigChanged: MutableList<(Map<String, Setting>) -> Unit> = mutableListOf()

    /**
     * This event is sent each time when the SDK evaluates a feature flag or setting. The event sends
     * the same evaluation details that you would get from [ConfigCatClient.getValueDetails].
     */
    public val onFlagEvaluated: MutableList<(EvaluationDetails) -> Unit> = mutableListOf()

    /**
     * This event is sent when an error occurs within the SDK.
     */
    public val onError: MutableList<(String) -> Unit> = mutableListOf()

    internal fun invokeOnReady() {
        for (method in onReady) {
            method()
        }
    }

    internal fun invokeOnConfigChanged(settings: Map<String, Setting>) {
        for (method in onConfigChanged) {
            method(settings)
        }
    }

    internal fun invokeOnFlagEvaluated(details: EvaluationDetails) {
        for (method in onFlagEvaluated) {
            method(details)
        }
    }

    internal fun invokeOnError(error: String) {
        for (method in onError) {
            method(error)
        }
    }

    internal fun clear() {
        onReady.clear()
        onConfigChanged.clear()
        onFlagEvaluated.clear()
        onError.clear()
    }
}