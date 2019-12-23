package com.aglushkov.repository.command

import androidx.lifecycle.LiveData

interface RepositoryCommand<T,R> {
    val liveData: LiveData<T>
    val commandId: R

    fun cancel()
}