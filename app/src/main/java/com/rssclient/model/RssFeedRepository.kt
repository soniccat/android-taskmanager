package com.rssclient.model

import androidx.lifecycle.MutableLiveData
import com.aglushkov.repository.RepositoryCommandHolder
import com.aglushkov.repository.command.CancellableRepositoryCommand
import com.aglushkov.repository.command.RepositoryCommand
import com.aglushkov.repository.livedata.Resource
import com.example.alexeyglushkov.cachemanager.ScopeStorageAdapter
import com.example.alexeyglushkov.cachemanager.Storage
import com.example.alexeyglushkov.streamlib.progress.ProgressListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
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

    fun getRssFeedLiveData(url: URL): MutableLiveData<Resource<RssFeed>> {
        return commandHolder.ensureLiveData(url.toString(), Resource.Uninitialized())
    }

    fun loadRssFeeds(progressListener: ProgressListener?): RepositoryCommand<Resource<List<RssFeed>>, String> {
        val job = scope.launch {
            load()
        }
        return commandHolder.putCommand(CancellableRepositoryCommand(LOAD_FEEDS_COMMAND, job, getFeedsLiveData()))
    }

    fun loadRssFeed(url: URL, progressListener: ProgressListener?): RepositoryCommand<Resource<RssFeed>, String> {
        val liveData = getRssFeedLiveData(url)
        liveData.value = Resource.Loading()

        val job = scope.launch {
            val feed = service.loadRss(url)
            liveData.postValue(Resource.Loaded(feed))
        }
        return commandHolder.putCommand(CancellableRepositoryCommand(url.toString(), job, getRssFeedLiveData(url)))
    }

    suspend fun addFeed(feed: RssFeed) {
        val feeds = getFeeds()
        val newFeeds = feeds + feed
        getFeedsLiveData().value = Resource.Loaded(newFeeds)
        save()
    }

    suspend fun removeFeed(feed: RssFeed) {
        val feeds = getFeeds()
        val newFeeds = feeds.filter { it != feed }
        getFeedsLiveData().value = Resource.Loaded(newFeeds)
        save()
    }

    suspend fun load() {
        getFeedsLiveData().postValue(Resource.Loading())
        val feeds = storage.getValue("feeds") as? List<RssFeed>
        if (feeds != null) {
            getFeedsLiveData().postValue(Resource.Loaded(feeds))
        } else {
            getFeedsLiveData().postValue(Resource.Uninitialized())
        }
    }

    suspend fun save() {
        storage.put("feeds", getFeeds(), null)
    }

    private fun getFeeds(): List<RssFeed> {
        val feedsLiveData = getFeedsLiveData()
        val feeds = feedsLiveData.value?.data() ?: emptyList()
        return feeds
    }
}