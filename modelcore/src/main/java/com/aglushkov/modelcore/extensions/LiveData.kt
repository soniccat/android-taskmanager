package com.aglushkov.modelcore.extensions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

/*
    Merge the current LiveData value with a related LiveData value and get a result LiveData
 */
fun <T, D> LiveData<T>.merge(liveData: LiveData<D>, merger: (tValue: T?, dValue: D?) -> T) : LiveData<T> {
    val result = MediatorLiveData<T>()
    result.addSource(this) { x -> result.setValue(merger(x, liveData.value)) }
    result.addSource(liveData) { x -> result.setValue(merger(this.value, x)) }
    return result
}
