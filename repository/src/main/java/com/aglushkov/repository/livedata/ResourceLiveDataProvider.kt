package com.aglushkov.repository.livedata

import androidx.lifecycle.LiveData

interface ResourceLiveDataProvider<T> {
    val liveData: LiveData<Resource<T>?>?
}