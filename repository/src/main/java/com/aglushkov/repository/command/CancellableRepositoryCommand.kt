package com.aglushkov.repository.command

import androidx.lifecycle.LiveData
import kotlinx.coroutines.Job

class CancellableRepositoryCommand<T, R>(id: R,
                                         val job: Job,
                                         liveData: LiveData<T>)
    : BaseRepositoryCommand<T, R>(id, liveData) {
    override fun cancel() {
        job.cancel()
    }

    suspend fun join() {
        job.join()
    }
}