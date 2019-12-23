package com.aglushkov.repository.command

import androidx.lifecycle.LiveData

open class BaseRepositoryCommand<T, R>(override val commandId: R, override val liveData: LiveData<T>) : RepositoryCommand<T, R> {
    override fun cancel() {}
}