package com.aglushkov.repository.command

import androidx.lifecycle.LiveData

interface RepositoryCommand<T> {
    val liveData: LiveData<T>
    val commandId: Long

    fun cancel()
}