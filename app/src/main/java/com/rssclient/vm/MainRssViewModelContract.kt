package com.rssclient.vm

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import com.aglushkov.repository.livedata.Resource
import com.aglushkov.taskmanager_http.image.Image
import java.net.URL

interface MainRssViewModelContract {

    // LiveData
    val feedLiveData: LiveData<Resource<List<RssItemView<*>>>>
    val errorLiveData: LiveData<ErrorViewModelContract.Error>

    // Events
    fun onAddRssFeedPressed(url: URL)
    fun onLoadImageRequested(image: Image,
                             completion: (bitmap: Bitmap?, error: Exception?) -> Unit)
}