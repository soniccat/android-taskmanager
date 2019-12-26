package com.rssclient.model

import androidx.lifecycle.MutableLiveData
import com.aglushkov.repository.RepositoryCommandHolder
import com.aglushkov.repository.command.CancellableRepositoryCommand
import com.aglushkov.repository.command.RepositoryCommand
import com.aglushkov.repository.livedata.Resource
import com.aglushkov.repository.livedata.load
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

    fun loadRssFeeds(progressListener: ProgressListener?): RepositoryCommand<Resource<List<RssFeed>>, String> {
        val liveData = getFeedsLiveData()
        val job = scope.launch {
            load(liveData)
        }
        return commandHolder.putCommand(CancellableRepositoryCommand(LOAD_FEEDS_COMMAND, job, liveData))
    }

    fun loadRssFeed(url: URL, progressListener: ProgressListener?): CancellableRepositoryCommand<Resource<RssFeed>, String> {
        val liveData = getFeedLiveData(url)
//        val initialValue = liveData.value
//        liveData.value = Resource.Loading()

//        val job = scope.launch {
//            try {
//                val feed = service.loadRss(url, progressListener)
//                liveData.postValue(Resource.Loaded(feed))
//            } catch (e: CancellationException) {
//                liveData.postValue(initialValue)
//                throw e
//            } catch (e: Exception) {
//                liveData.postValue(Resource.Error(e, true))
//            }
//        }

        val job = scope.launch {
            service.loadRss(url, progressListener)
        }

        return commandHolder.putCommand(CancellableRepositoryCommand(url.toString(), job, getFeedLiveData(url)))
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

    private suspend fun load(liveData: MutableLiveData<Resource<List<RssFeed>>>) {
//        val initialValue = liveData.value
//        val loadingRes = liveData.value?.toLoading() ?: Resource.Loading()
//        liveData.postValue(loadingRes)
//
//        try {
//            val feeds = storage.getValue("feeds") as? List<RssFeed>
//            val newStatus: Resource<List<RssFeed>> = if (feeds != null) {
//                Resource.Loaded(feeds)
//            } else {
//                Resource.Uninitialized()
//            }
//
//            liveData.postValue(newStatus)
//        } catch (e: CancellationException) {
//            liveData.postValue(initialValue)
//            throw e
//        } catch (e: Exception) {
//            val errorRes = initialValue?.toError(e, true) ?: Resource.Error(e, true)
//            liveData.postValue(errorRes)
//        }

        liveData.load {
            storage.getValue("feeds") as? List<RssFeed>
        }
    }

    private suspend fun save() {
        storage.put("feeds", getFeeds(), null)
    }

    private fun getFeeds(): List<RssFeed> {
        val feedsLiveData = getFeedsLiveData()
        return feedsLiveData.value?.data() ?: emptyList()
    }
}