package com.configcat

import kotlinx.atomicfu.locks.ReentrantLock
import kotlinx.atomicfu.locks.reentrantLock
import kotlinx.atomicfu.locks.withLock

/**
 * Events fired by [ConfigCatClient].
 */
public class Hooks {
    private val onReady: MutableList<() -> Unit> = mutableListOf()
    private val onConfigChanged: MutableList<(Map<String, Setting>) -> Unit> = mutableListOf()
    private val onFlagEvaluated: MutableList<(EvaluationDetails) -> Unit> = mutableListOf()
    private val onError: MutableList<(String) -> Unit> = mutableListOf()
    private val lock: ReentrantLock = reentrantLock()

    /**
     * This event is sent when the SDK reaches the ready state.
     * If the SDK is configured with lazy load or manual polling it's considered ready right after instantiation.
     * If it's using auto polling, the ready state is reached when the SDK has a valid config.json loaded into
     * memory either from cache or from HTTP. If the config couldn't be loaded neither from cache nor
     * from HTTP the `onClientReady` event fires when the auto polling's `maxInitWaitTimeInSeconds` is reached.
     */
    public fun addOnReady(handler: () -> Unit) {
        lock.withLock {
            onReady.add(handler)
        }
    }

    /**
     * This event is sent when the SDK loads a valid config.json into memory from cache,
     * and each subsequent time when the loaded config.json changes via HTTP.
     */
    public fun addOnConfigChanged(handler: (Map<String, Setting>) -> Unit) {
        lock.withLock {
            onConfigChanged.add(handler)
        }
    }

    /**
     * This event is sent each time when the SDK evaluates a feature flag or setting. The event sends
     * the same evaluation details that you would get from [ConfigCatClient.getValueDetails].
     */
    public fun addOnFlagEvaluated(handler: (EvaluationDetails) -> Unit) {
        lock.withLock {
            onFlagEvaluated.add(handler)
        }
    }

    /**
     * This event is sent when an error occurs within the SDK.
     */
    public fun addOnError(handler: (String) -> Unit) {
        lock.withLock {
            onError.add(handler)
        }
    }

    internal fun invokeOnReady() {
        lock.withLock {
            for (method in onReady) {
                method()
            }
        }
    }

    internal fun invokeOnConfigChanged(settings: Map<String, Setting>) {
        lock.withLock {
            for (method in onConfigChanged) {
                method(settings)
            }
        }
    }

    internal fun invokeOnFlagEvaluated(details: EvaluationDetails) {
        lock.withLock {
            for (method in onFlagEvaluated) {
                method(details)
            }
        }
    }

    internal fun invokeOnError(error: String) {
        lock.withLock {
            for (method in onError) {
                method(error)
            }
        }
    }

    internal fun clear() {
        lock.withLock {
            onReady.clear()
            onConfigChanged.clear()
            onFlagEvaluated.clear()
            onError.clear()
        }
    }
}
