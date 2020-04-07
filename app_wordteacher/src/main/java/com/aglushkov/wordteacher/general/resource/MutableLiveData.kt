package com.aglushkov.wordteacher.general.resource

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.Exception

fun <T> MutableLiveData<Resource<T>>.load(scope: CoroutineScope,
                                          canTryAgain: Boolean,
                                          loader: suspend () -> T?): Job {
    val initialValue = value
    val loadingRes = value?.toLoading() ?: Resource.Loading()
    value = loadingRes

    return scope.launch {
        try {
            val result = loader()
            val newStatus: Resource<T> = if (result != null) {
                Resource.Loaded(result)
            } else {
                Resource.Uninitialized()
            }

            if (value == loadingRes) {
                postValue(newStatus)
            }
        } catch (e: CancellationException) {
            if (value == loadingRes) {
                postValue(initialValue)
            }
            throw e
        } catch (e: Exception) {
            if (value == loadingRes) {
                val errorRes = initialValue?.toError(e, canTryAgain) ?: Resource.Error(e, canTryAgain)
                postValue(errorRes)
            }
        }
    }
}