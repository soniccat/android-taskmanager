package com.rssclient.rssfeeditems.vm

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import com.aglushkov.repository.livedata.Resource
import com.aglushkov.taskmanager_http.image.Image
import com.example.alexeyglushkov.taskmanager.task.TaskManagerSnapshot
import com.rssclient.model.RssFeed
import com.rssclient.model.RssItem
import com.rssclient.vm.RssView

interface RssItemsViewModelContract {
    // LiveData
    val rssItems: LiveData<Resource<List<RssView<*>>>>
    val taskManagerSnapshot: LiveData<TaskManagerSnapshot>

    // Events
    fun onRssItemPressed(rssItem: RssItem)
    fun onLoadImageRequested(image: Image,
                             completion: (bitmap: Bitmap?, error: Exception?) -> Unit)
}