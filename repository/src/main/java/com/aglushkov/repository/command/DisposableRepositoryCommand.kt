package com.aglushkov.repository.command

import androidx.lifecycle.LiveData
import io.reactivex.disposables.Disposable

class DisposableRepositoryCommand<T, R>(id: R,
                                        private val disposable: Disposable,
                                        liveData: LiveData<T>)
    : BaseRepositoryCommand<T, R>(id, liveData) {
    override fun cancel() {
        disposable.dispose()
    }
}