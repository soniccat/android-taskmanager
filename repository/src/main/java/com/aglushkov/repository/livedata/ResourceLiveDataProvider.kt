package com.aglushkov.repository.livedata

import androidx.lifecycle.LiveData
import com.aglushkov.modelcore.resource.Resource

interface ResourceLiveDataProvider<T> {
    val liveData: LiveData<Resource<T>>
}