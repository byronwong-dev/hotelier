package com.byron.hotelier.controller

import com.byron.hotelier.adapter.domain.Hotel
import io.ktor.client.HttpClient
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

private const val TTL_MS = 5_000L

class SupplierDataSource(private val client: HttpClient, private val maxRetries: Int) {
    private val mutex = Mutex()
    private val cache = mutableMapOf<HotelSupplier, CacheEntry<List<Hotel>>>()

    suspend fun fetch(supplier: HotelSupplier): List<Hotel> =
        mutex.withLock {
            cache[supplier]?.takeIf { it.isValid() }?.value ?: run {
                val hotels = fetchWithRetry(supplier)
                cache[supplier] = CacheEntry(hotels)
                hotels
            }
        }

    private suspend fun fetchWithRetry(supplier: HotelSupplier): List<Hotel> {
        var lastException: Exception? = null
        repeat(maxRetries) {
            try {
                return supplier.fetcher(client)
            } catch (e: Exception) {
                lastException = e
            }
        }
        throw lastException!!
    }
}

private data class CacheEntry<T>(
    val value: T,
    val fetchedAt: Long = System.currentTimeMillis(),
) {
    fun isValid() = System.currentTimeMillis() - fetchedAt < TTL_MS
}
