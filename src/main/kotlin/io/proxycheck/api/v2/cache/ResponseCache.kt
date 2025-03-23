package io.proxycheck.api.v2.cache

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

/**
 * A simple in-memory cache for API responses with adjustable expiration time.
 */
class ResponseCache<T> {
    private data class CacheEntry<T>(
        val data: T,
        val expirationTime: Long
    )

    private val cache = ConcurrentHashMap<String, CacheEntry<T>>()

    /**
     * Puts a value in the cache with the specified key and expiration time.
     *
     * @param key The key to store the value under.
     * @param value The value to store.
     * @param expirationTime The time after which the cached value should expire.
     * @param timeUnit The time unit of the expiration time.
     */
    fun put(key: String, value: T, expirationTime: Long, timeUnit: TimeUnit) {
        val expirationTimeMillis = System.currentTimeMillis() + timeUnit.toMillis(expirationTime)
        cache[key] = CacheEntry(value, expirationTimeMillis)
    }

    /**
     * Gets a value from the cache if it exists and has not expired.
     *
     * @param key The key to retrieve the value for.
     * @return The cached value, or null if the key is not in the cache or the value has expired.
     */
    fun get(key: String): T? {
        val entry = cache[key] ?: return null
        
        return if (System.currentTimeMillis() < entry.expirationTime) {
            entry.data
        } else {
            cache.remove(key)
            null
        }
    }

    /**
     * Removes a value from the cache.
     *
     * @param key The key to remove.
     */
    fun remove(key: String) {
        cache.remove(key)
    }

    /**
     * Clears all entries from the cache.
     */
    fun clear() {
        cache.clear()
    }

    /**
     * Gets the number of entries in the cache.
     *
     * @return The number of entries in the cache.
     */
    fun size(): Int {
        return cache.size
    }
}
