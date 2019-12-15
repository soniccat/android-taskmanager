package com.example.alexeyglushkov.cachemanager

import com.example.alexeyglushkov.streamlib.progress.ProgressUpdater
import io.reactivex.Completable

/**
 * Created by alexeyglushkov on 10.10.15.
 */
interface StorageCleaner {
    fun setProgressInfo(info: ProgressUpdater?)
    fun clean(storage: Storage): Completable
    fun clean(storage: RxStorage): Completable
}