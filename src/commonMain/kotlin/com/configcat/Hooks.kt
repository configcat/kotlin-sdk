package com.configcat

import com.configcat.model.Setting
import kotlinx.atomicfu.locks.ReentrantLock
import kotlinx.atomicfu.locks.reentrantLock
import kotlinx.atomicfu.locks.withLock
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.AtomicReference

/**
 * Events fired by [ConfigCatClient].
 */
public class Hooks {
    private val isClientReady = AtomicBoolean(false)
    private val clientCacheState = AtomicReference(ClientCacheState.NO_FLAG_DATA)
    private val onClientReadyWithState: MutableList<(ClientCacheState) -> Unit> = mutableListOf()
    private val onClientReadyWithSnapshot: MutableList<(ConfigCatClientSnapshot) -> Unit> = mutableListOf()
    private val onConfigChanged: MutableList<
        (
            Map<String, Setting>,
            ConfigCatClientSnapshot,
        ) -> Unit,
    > = mutableListOf()
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
    public fun addOnClientReady(handler: (ClientCacheState) -> Unit) {
        lock.withLock {
            if (isClientReady.load()) {
                handler(clientCacheState.load())
            } else {
                onClientReadyWithState.add(handler)
            }
        }
    }

    /**
     * This event is sent when the SDK reaches the ready state.
     * If the SDK is configured with lazy load or manual polling it's considered ready right after instantiation.
     * If it's using auto polling, the ready state is reached when the SDK has a valid config.json loaded into
     * memory either from cache or from HTTP. If the config couldn't be loaded neither from cache nor
     * from HTTP the `onClientReady` event fires when the auto polling's `maxInitWaitTimeInSeconds` is reached.
     *
     * Late subscriptions (through the `client.hooks` property) might not get notified
     * if the client reached the ready state before the subscription.
     */
    public fun addOnClientReadyWithSnapshot(snapshotHandler: (ConfigCatClientSnapshot) -> Unit) {
        lock.withLock {
            onClientReadyWithSnapshot.add(snapshotHandler)
        }
    }

    /**
     * This event is sent when the SDK loads a valid config.json into memory from cache,
     * and each subsequent time when the loaded config.json changes via HTTP.
     */
    public fun addOnConfigChanged(handler: (Map<String, Setting>, ConfigCatClientSnapshot) -> Unit) {
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

    internal fun invokeOnClientReady(
        snapshotBuilder: SnapshotBuilder,
        inMemoryResult: InMemoryResult,
    ) {
        lock.withLock {
            this.isClientReady.store(true)
            this.clientCacheState.store(inMemoryResult.cacheState)
            for (method in onClientReadyWithState) {
                method(inMemoryResult.cacheState)
            }
            if (onClientReadyWithSnapshot.isNotEmpty()) {
                val snapshot = snapshotBuilder.buildSnapshot(inMemoryResult)
                for (method in onClientReadyWithSnapshot) {
                    method(snapshot)
                }
            }
        }
    }

    internal fun invokeOnConfigChanged(
        snapshotBuilder: SnapshotBuilder,
        inMemoryResult: InMemoryResult,
    ) {
        lock.withLock {
            if (onConfigChanged.isNotEmpty()) {
                val snapshot = snapshotBuilder.buildSnapshot(inMemoryResult)
                for (method in onConfigChanged) {
                    method(inMemoryResult.settingResult.settings, snapshot)
                }
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
            onClientReadyWithSnapshot.clear()
            onClientReadyWithState.clear()
            onConfigChanged.clear()
            onFlagEvaluated.clear()
            onError.clear()
        }
    }
}
