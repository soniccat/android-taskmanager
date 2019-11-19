package com.aglushkov.repository.command

import androidx.lifecycle.LiveData

open class BaseRepositoryCommand<T>(override val commandId: Long, override val liveData: LiveData<T>) : RepositoryCommand<T> {
    override fun cancel() {}
}