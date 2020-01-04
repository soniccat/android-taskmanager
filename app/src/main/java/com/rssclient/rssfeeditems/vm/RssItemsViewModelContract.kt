package com.rssclient.rssfeeditems.vm

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.aglushkov.repository.livedata.Resource
import com.aglushkov.taskmanager_http.image.Image
import com.example.alexeyglushkov.taskmanager.snapshot.TaskManagerSnapshot
import com.example.alexeyglushkov.tools.Range
import com.rssclient.model.RssItem
import com.rssclient.vm.RssViewItem

interface RssItemsViewModelContract {
    data class ImageInfo(val image: Image,
                         val imageTag: String, // hash code of image.url instead of passing image
                         val position: Int,
                         val visibleRange: Range<Int>)

    // LiveData
    val rssItems: LiveData<Resource<List<RssViewItem<*>>>>
    val taskManagerSnapshot: LiveData<TaskManagerSnapshot>

    fun getImageProgressLiveData(imageTag: String): MutableLiveData<Float>

    // Events
    fun onRssItemPressed(rssItem: RssItem)
    fun onScrolled(visibleRange: Range<Int>)
    fun onLoadImageRequested(imageInfo: ImageInfo,
                             completion: (bitmap: Bitmap?, error: Exception?) -> Unit)
}