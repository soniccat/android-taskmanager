package com.aglushkov.repository.livedata

import androidx.lifecycle.LiveData

class NonNullLiveData<T>(value: T) : LiveData<T>() {
    init {
        setValue(value)
    }
}