package com.aglushkov.wordteacher.model


sealed class Resource<T>(needShowNext: Boolean) {
    val canLoadNextPage: Boolean = needShowNext

    class Uninitialized<T> : Resource<T>(false)
    class Loaded<T>(val data: T, canLoadNext: Boolean = false) : Resource<T>(canLoadNext)
    class Loading<T>(val data: T? = null, canLoadNext: Boolean = false) : Resource<T>(canLoadNext)
    class Error<T>(val throwable: Throwable, val canTryAgain: Boolean, val data: T? = null, canLoadNext: Boolean = false) : Resource<T>(canLoadNext)

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
            is Loaded -> Loaded(data!!, canLoadNextPage)
            is Loading -> Loading(data, canLoadNextPage)
            is Error -> Error(throwable, canTryAgain, data, canLoadNextPage)
        }
    }

    fun <R> copyWith(data: R?): Resource<R> {
        return when(this) {
            is Uninitialized -> Uninitialized()
            is Loaded -> Loaded(data!!, canLoadNextPage)
            is Loading -> Loading(data, canLoadNextPage)
            is Error -> Error(throwable, canTryAgain, data, canLoadNextPage)
        }
    }
}

fun Resource<*>?.isNotLoadedAndNotLoading(): Boolean {
    return if (this == null) false else this !is Resource.Loaded && this !is Resource.Loading
}