package com.rssclient.model

import androidx.lifecycle.MutableLiveData
import com.aglushkov.modelcore.resource.Resource
import com.aglushkov.modelcore.resource.load
import com.aglushkov.repository.RepositoryCommandHolder
import com.aglushkov.repository.command.CancellableRepositoryCommand
import com.aglushkov.repository.command.RepositoryCommand
import com.example.alexeyglushkov.cachemanager.ScopeStorageAdapter
import com.example.alexeyglushkov.cachemanager.Storage
import com.example.alexeyglushkov.streamlib.progress.ProgressListener
import kotlinx.coroutines.*
import java.net.URL

class RssFeedRepository(val service: RssFeedService, storage: Storage) {
    companion object {
        private const val LOAD_FEEDS_COMMAND = "LOAD_FEEDS_COMMAND"
    }

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val commandHolder = RepositoryCommandHolder<String>()
    private val storage: ScopeStorageAdapter

    init {
        this.storage = ScopeStorageAdapter(storage, scope)
        loadRssFeeds(null)
    }

    fun getFeedsLiveData(): MutableLiveData<Resource<List<RssFeed>>> {
        return commandHolder.ensureLiveData(LOAD_FEEDS_COMMAND, Resource.Uninitialized())
    }

    fun getFeedLiveData(url: URL): MutableLiveData<Resource<RssFeed>> {
        return commandHolder.ensureLiveData(url.toString(), Resource.Uninitialized())
    }

    fun getImageProgressLiveData(tag: String): MutableLiveData<Float> {
        return commandHolder.ensureLiveData(tag, 0.0f)
    }

    fun loadRssFeeds(progressListener: ProgressListener?): RepositoryCommand<Resource<List<RssFeed>>, String> {
        val liveData = getFeedsLiveData()
        val job = liveData.load(scope, true) {
            storage.getValue("feeds") as? List<RssFeed>
        }

        return commandHolder.putCommand(CancellableRepositoryCommand(LOAD_FEEDS_COMMAND, job, liveData))
    }

    suspend fun addRssFeed(url: URL, progressListener: ProgressListener?) {
        val command = loadRssFeed(url, progressListener)
        command.await()?.data()?.let {
            addFeed(it)
        }
    }

    fun loadRssFeed(url: URL, progressListener: ProgressListener?): CancellableRepositoryCommand<Resource<RssFeed>, String> {
        val liveData = getFeedLiveData(url)
        val job = liveData.load(scope, true) {
            service.loadRss(url, progressListener)
        }

        return commandHolder.putCommand(CancellableRepositoryCommand(url.toString(), job, getFeedLiveData(url)))
    }

    suspend fun addFeed(feed: RssFeed) {
        val feeds = getFeeds()
        withContext(scope.coroutineContext) {
            val newFeeds = feeds + feed
            save(newFeeds)
            getFeedsLiveData().postValue(Resource.Loaded(newFeeds))
        }
    }

    suspend fun removeFeed(feed: RssFeed) {
        val feeds = getFeeds()
        withContext(scope.coroutineContext) {
            val newFeeds = feeds.filter { it != feed }
            save(newFeeds)
            getFeedsLiveData().postValue(Resource.Loaded(newFeeds))
        }
    }

    private suspend fun save(feeds: List<RssFeed>) {
        storage.put("feeds", feeds, null)
    }

    private fun getFeeds(): List<RssFeed> {
        val feedsLiveData = getFeedsLiveData()
        return feedsLiveData.value?.data() ?: emptyList()
    }
}