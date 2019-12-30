package com.rssclient.rssfeed.vm

import android.app.Application
import android.graphics.Bitmap
import androidx.annotation.StringRes
import androidx.lifecycle.*
import com.aglushkov.repository.livedata.Resource
import com.aglushkov.taskmanager_http.image.Image
import com.aglushkov.taskmanager_http.image.ImageLoader
import com.example.alexeyglushkov.ext.getResString
import com.example.alexeyglushkov.taskmanager.task.Tasks
import com.main.MainApplication
import com.rssclient.model.RssFeed
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import java.lang.Exception
import java.net.URL
import com.rssclient.controllers.R
import com.rssclient.vm.ErrorViewModelContract
import com.rssclient.vm.RssItemView

class MainRssViewModel(application: MainApplication): AndroidViewModel(application), MainRssViewModelContract {
    private var taskManager = application.taskManager
    private var rssRepository = application.rssRepository

    override val feedLiveData: LiveData<Resource<List<RssItemView<*>>>>
        get() = rssRepository.getFeedsLiveData().map {
            val convertedData: List<RssItemView<*>> = it.data()?.map { RssItemView.RssFeedView(it) } ?: emptyList()
            it.copyWith(convertedData)
        }

    override val eventLiveData = MutableLiveData<MainRssViewModelContract.Event>()
    override val errorLiveData = MutableLiveData<ErrorViewModelContract.Error>()

    init {
        rssRepository.loadRssFeeds(null)
    }

    fun getFeedLiveData(url: URL): LiveData<Resource<RssFeed>> {
        return rssRepository.getFeedLiveData(url)
    }

    // Events

    override fun onAddRssFeedPressed(url: URL) {
        loadRssFeed(url)
    }

    override fun onRssFeedLongPressed(feed: RssFeed) {
        eventLiveData.value = MainRssViewModelContract.Event.ShowActionMode(feed)
    }

    override fun onRssFeedDelete(feed: RssFeed) {
        viewModelScope.launch {
            try {
                rssRepository.removeFeed(feed)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                postError(R.string.save_error)
            }
        }
    }

    override fun onRssFeedPressed(feed: RssFeed) {
        eventLiveData.value = MainRssViewModelContract.Event.OpenRssFeed(feed)
    }

    override fun onLoadImageRequested(image: Image, completion: (bitmap: Bitmap?, error: Exception?) -> Unit) {
        val task = ImageLoader().buildBitmapTask(taskManager.threadRunner, image)
        viewModelScope.launch {
            try {
                val bitmap = Tasks.run<Bitmap>(task, taskManager)
                completion(bitmap, null)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                completion(null, e)
            }
        }
    }

    // Actions

    private fun loadRssFeed(url: URL) {
        viewModelScope.launch {
            try {
                val cmd = rssRepository.loadRssFeed(url, null)
                cmd.await()?.data()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                postError(R.string.load_error)
            }
        }
    }

    private fun postError(@StringRes descriptionRes: Int) {
        val descr = getApplication<Application>().getResString(descriptionRes)
        val ok = getApplication<Application>().getResString(R.string.ok)
        errorLiveData.postValue(ErrorViewModelContract.Error(null,
                descr,
                ErrorViewModelContract.ErrorAction(ok, {}),
                null))
    }
}