package com.rssclient.rssfeed.vm

import android.graphics.Bitmap
import android.os.Bundle
import androidx.lifecycle.LiveData
import com.aglushkov.repository.livedata.Resource
import com.aglushkov.taskmanager_http.image.Image
import com.rssclient.model.RssFeed
import com.rssclient.vm.ErrorViewModelContract
import com.rssclient.vm.RssViewItem
import java.net.URL

interface MainRssViewModelContract {
    sealed class Event: com.rssclient.vm.Event() {
        class ShowActionMode(val feed: RssFeed): Event()
        class OpenRssFeed(val extras: Bundle): Event()
    }

    // LiveData
    val feedLiveData: LiveData<Resource<List<RssViewItem<*>>>>
    val eventLiveData: LiveData<Event>
    val errorLiveData: LiveData<ErrorViewModelContract.Error>

    // Events
    fun onAddRssFeedPressed(url: URL)
    fun onRssFeedLongPressed(feed: RssFeed)
    fun onRssFeedDelete(feed: RssFeed)
    fun onRssFeedPressed(feed: RssFeed)
    fun onLoadImageRequested(image: Image,
                             completion: (bitmap: Bitmap?, error: Exception?) -> Unit)
}