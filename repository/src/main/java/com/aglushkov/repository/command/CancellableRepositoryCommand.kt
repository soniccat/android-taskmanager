package com.aglushkov.repository.command

import androidx.lifecycle.LiveData
import kotlinx.coroutines.Job

class CancellableRepositoryCommand<T>(id: Long, private val job: Job, liveData: LiveData<T>) : BaseRepositoryCommand<T>(id, liveData) {
    override fun cancel() {
        job.cancel()
    }
}