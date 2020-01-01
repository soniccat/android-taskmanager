package com.rssclient.rssfeeditems.vm

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.aglushkov.repository.livedata.Resource
import com.aglushkov.taskmanager_http.image.Image
import com.example.alexeyglushkov.taskmanager.task.TaskManagerSnapshot
import com.example.alexeyglushkov.tools.Range
import com.rssclient.model.RssItem
import com.rssclient.vm.RssView

interface RssItemsViewModelContract {
    data class ImageInfo(val image: Image, val tag: Any, val position: Int, val visibleRange: Range<Int>)

    // LiveData
    val rssItems: LiveData<Resource<List<RssView<*>>>>
    val taskManagerSnapshot: LiveData<TaskManagerSnapshot>

    fun getImageProgressLiveData(tag: Int): MutableLiveData<Float>

    // Events
    fun onRssItemPressed(rssItem: RssItem)
    fun onLoadImageRequested(imageInfo: ImageInfo,
                             completion: (bitmap: Bitmap?, error: Exception?) -> Unit)
}