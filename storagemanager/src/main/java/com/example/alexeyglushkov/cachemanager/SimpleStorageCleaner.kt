package com.example.alexeyglushkov.cachemanager

import com.example.alexeyglushkov.streamlib.progress.ProgressUpdater
import io.reactivex.Completable

/**
 * Created by alexeyglushkov on 10.10.15.
 */
class SimpleStorageCleaner : StorageCleaner {
    override fun setProgressInfo(info: ProgressUpdater?) {}
    override fun clean(storage: Storage): Completable {
        val asyncStorage = RxStorageAdapter(storage)
        return clean(asyncStorage)
    }

    override fun clean(storage: RxStorage): Completable {
        return storage.getEntries()
                .doOnSuccess { entries ->
                    for (entry in entries) {
                        val metadata = entry.metadata
                        if (metadata != null && metadata.isExpired()) {
                            try {
                                entry.delete()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
                .ignoreElement()
    }
}