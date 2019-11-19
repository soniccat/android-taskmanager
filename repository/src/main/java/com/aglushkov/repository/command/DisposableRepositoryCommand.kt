package com.aglushkov.repository.command

import androidx.lifecycle.LiveData
import io.reactivex.disposables.Disposable

class DisposableRepositoryCommand<T>(id: Long, private val disposable: Disposable, liveData: LiveData<T>) : BaseRepositoryCommand<T>(id, liveData) {
    override fun cancel() {
        disposable.dispose()
    }
}