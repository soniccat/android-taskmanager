package com.aglushkov.taskmanager_http.loader.http

import android.util.Log
import com.example.alexeyglushkov.cachemanager.clients.Cache
import com.example.alexeyglushkov.cachemanager.clients.Cache.CacheEmptyException
import com.example.alexeyglushkov.cachemanager.clients.CacheStatus
import com.example.alexeyglushkov.streamlib.data_readers_and_writers.InputStreamDataReaders
import java.io.ByteArrayInputStream

/**
 * Created by alexeyglushkov on 26.11.15.
 */
open class HttpCacheableTransport<T>
    @JvmOverloads constructor(provider: HttpURLConnectionProvider,
                              handler: HTTPConnectionBytesReader<T>,
                              var cache: Cache? = null) : HttpBytesTransport<T>(provider, handler) {
    private var needStore = false
    private val cacheKey: String
        get() = provider.url.toString()

    override suspend fun start() {
        var isCacheLoaded = false
        if (CacheStatus.canLoadFromCache(cache)) {
            try {
                isCacheLoaded = handleCacheContent()
            } catch (ex: Exception) {
                Log.e(TAG, "handleCacheContent exception")
                ex.printStackTrace()
            }
        }
        val canStartLoading = cache == null || cache!!.cacheMode != Cache.CacheMode.ONLY_LOAD_FROM_CACHE
        if (canStartLoading && !isCacheLoaded) {
            needStore = CacheStatus.canWriteToCache(cache)
            super.start()
        }
    }

    @Throws(Exception::class)
    private fun handleCacheContent(): Boolean {
        var isLoaded = false
        val safeCacheClient = cache
        if (safeCacheClient != null && applyCacheContent(safeCacheClient)) {
            isLoaded = true
        } else if (safeCacheClient != null && safeCacheClient.cacheMode == Cache.CacheMode.ONLY_LOAD_FROM_CACHE) {
            _error = CacheEmptyException(cacheKey, null)
        }
        return isLoaded
    }

    @Throws(Exception::class)
    private fun applyCacheContent(client: Cache): Boolean {
        var isApplied = false
        val bytes = getCachedBytes(client)
        if (bytes != null) {
            val result = InputStreamDataReaders.readOnce(byteArrayReader, ByteArrayInputStream(bytes))
            onDataLoaded(result)
            isApplied = true
        }
        return isApplied
    }

    @Throws(Exception::class)
    private fun getCachedBytes(client: Cache): ByteArray? {
        return client.getCachedValue<Any>(cacheKey) as ByteArray?
    }

    override fun onDataLoaded(handledData: T?) {
        super.onDataLoaded(handledData)
        if (needStore && cache != null) {
            try {
                cache!!.putValue(cacheKey, byteArrayReader.byteArray)
            } catch (e: Exception) {
                Log.e(TAG, "cache.put exception")
                e.printStackTrace()
            }
        }
    }

    companion object {
        private const val TAG = "HTTPCacheableTr"
    }

}