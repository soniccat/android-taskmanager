package com.aglushkov.repository.livedata

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import java.lang.Exception

sealed class Resource<T>(needShowNext: Boolean) {
    val canLoadNextPage: Boolean = needShowNext

    class Uninitialized<T> : Resource<T>(false)
    class Restored<T>(val data: T?, canLoadNext: Boolean = false) : Resource<T>(canLoadNext)
    class Loaded<T>(val data: T, canLoadNext: Boolean = false) : Resource<T>(canLoadNext)
    class Loading<T>(val data: T? = null, canLoadNext: Boolean = false) : Resource<T>(canLoadNext)
    class Error<T>(val throwable: Throwable, val canTryAgain: Boolean, val data: T? = null, canLoadNext: Boolean = false) : Resource<T>(canLoadNext)

    fun toRestored(data: T? = data(), canLoadNext: Boolean = this.canLoadNextPage) = Restored(data, canLoadNext)
    fun toLoading(data: T? = data(), canLoadNext: Boolean = this.canLoadNextPage) = Loading(data, canLoadNext)
    fun toLoaded(data: T, canLoadNext: Boolean = this.canLoadNextPage) = Loaded(data, canLoadNext)
    fun toError(throwable: Throwable, canTryAgain: Boolean, data: T? = data(), canLoadNext: Boolean = this.canLoadNextPage) = Error(throwable, canTryAgain, data, canLoadNext)

    // Getters

    fun isUninitialized(): Boolean {
        return when(this) {
            is Uninitialized -> true
            else -> false
        }
    }

    fun data(): T? {
        val data = when (this) {
            is Restored -> data
            is Loaded -> data
            is Loading -> data
            is Error -> data
            else -> null
        }
        return data
    }

    fun copy(data: T? = this.data()): Resource<T> {
        return when(this) {
            is Uninitialized -> Uninitialized()
            is Restored -> Restored(data, canLoadNextPage)
            is Loaded -> Loaded(data!!, canLoadNextPage)
            is Loading -> Loading(data, canLoadNextPage)
            is Error -> Error(throwable, canTryAgain, data, canLoadNextPage)
        }
    }

    fun <R> copyWith(data: R?): Resource<R> {
        return when(this) {
            is Uninitialized -> Uninitialized()
            is Restored -> Restored(data, canLoadNextPage)
            is Loaded -> Loaded(data!!, canLoadNextPage)
            is Loading -> Loading(data, canLoadNextPage)
            is Error -> Error(throwable, canTryAgain, data, canLoadNextPage)
        }
    }
}

fun Resource<*>?.isUninitialized(): Boolean {
    return this?.isUninitialized() ?: true
}

suspend fun <T> MutableLiveData<Resource<T>>.load(loader: suspend () -> T?) {
    val initialValue = value
    val loadingRes = value?.toLoading() ?: Resource.Loading()
    postValue(loadingRes)

    try {
        val result = loader()
        val newStatus: Resource<T> = if (result != null) {
            Resource.Loaded(result)
        } else {
            Resource.Uninitialized()
        }

        postValue(newStatus)
    } catch (e: CancellationException) {
        if (value == loadingRes) {
            postValue(initialValue)
        }
        throw e
    } catch (e: Exception) {
        val errorRes = initialValue?.toError(e, true) ?: Resource.Error(e, true)
        postValue(errorRes)
    }
}
