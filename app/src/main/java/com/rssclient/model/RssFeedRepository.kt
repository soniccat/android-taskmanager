package com.rssclient.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.aglushkov.repository.livedata.NonNullLiveData
import com.aglushkov.repository.livedata.NonNullMutableLiveData
import com.aglushkov.repository.livedata.Resource

class RssFeedRepository {
    var _feed = MutableLiveData<Resource<RssFeed>>(Resource.Uninitialized())
    val feed: LiveData<Resource<RssFeed>>
        get() = _feed


}