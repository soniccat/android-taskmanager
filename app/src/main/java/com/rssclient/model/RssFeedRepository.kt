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

class RssFeedRepository(val service: RssFeedService, storage: Storage) {
    companion object {
        private const val LOAD_FEEDS_COMMAND: Long = 0
        private const val LOAD_FEED_COMMAND_PREFIX: Long = 2 // it's 2 to support -1 set id
    }

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val commandHolder = RepositoryCommandHolder()
    private val storage: ScopeStorageAdapter

    init {
        this.storage = ScopeStorageAdapter(storage, scope)
        loadRssFeeds(null)
    }

    fun getFeedsLiveData(): MutableLiveData<Resource<List<RssFeed>>> {
        return commandHolder.ensureLiveData(LOAD_FEEDS_COMMAND, Resource.Uninitialized())
    }

    fun getRssFeedLiveData(setId: Long): MutableLiveData<Resource<RssFeed>> {
        return commandHolder.ensureLiveData(LOAD_FEED_COMMAND_PREFIX + setId, Resource.Uninitialized())
    }

    fun loadRssFeeds(progressListener: ProgressListener?): RepositoryCommand<Resource<List<RssFeed>>> {
        val job = scope.launch {
            load()
        }
        return commandHolder.putCommand(CancellableRepositoryCommand(LOAD_FEEDS_COMMAND, job, getFeedsLiveData()))
    }

    fun loadRssFeed(id: Long, progressListener: ProgressListener?): RepositoryCommand<Resource<RssFeed>> {
        val liveData = getRssFeedLiveData(id)
        liveData.value = Resource.Loading()

        val job = scope.launch {
            val feed = service.loadRss("")
            liveData.postValue(Resource.Loaded(feed))
        }
        return commandHolder.putCommand(CancellableRepositoryCommand(LOAD_FEED_COMMAND_PREFIX + id, job, getRssFeedLiveData(id)))
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