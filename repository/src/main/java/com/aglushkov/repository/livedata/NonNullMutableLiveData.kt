package com.aglushkov.repository.livedata

import androidx.lifecycle.MutableLiveData

class NonNullMutableLiveData<T>(value: T) : MutableLiveData<T>() {
    init {
        setValue(value)
    }
}