package com.example.alexeyglushkov.cachemanager

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.SingleOnSubscribe

class RxStorageAdapter(private val storage: Storage) : RxStorage {
    override fun put(key: String, value: Any, metadata: StorageMetadata): Completable {
        return Completable.create { emitter ->
            try {
                storage.put(key, value, metadata)
                emitter.onComplete()
            } catch (e: Exception) {
                emitter.onError(e)
            }
        }
    }

    override fun getValue(key: String): Maybe<Any> {
        return Maybe.create { emitter ->
            try {
                val result = storage.getValue(key)
                if (result != null) {
                    emitter.onSuccess(result)
                } else {
                    emitter.onComplete()
                }
            } catch (e: Exception) {
                emitter.onError(e)
            }
        }
    }

    override fun getMetadata(key: String): Maybe<StorageMetadata> {
        return Maybe.create { emitter ->
            try {
                val metadata = storage.getMetadata(key)
                if (metadata != null) {
                    emitter.onSuccess(metadata)
                } else {
                    emitter.onComplete()
                }
            } catch (e: Exception) {
                emitter.onError(e)
            }
        }
    }

    override fun remove(key: String): Completable {
        return Completable.create { emitter ->
            try {
                storage.remove(key)
                emitter.onComplete()
            } catch (e: Exception) {
                emitter.onError(e)
            }
        }
    }

    override fun getEntry(key: String): Maybe<StorageEntry> {
        return Maybe.create { emitter ->
            try {
                val entry = storage.getEntry(key)
                if (entry != null) {
                    emitter.onSuccess(entry)
                } else {
                    emitter.onComplete()
                }
            } catch (e: Exception) {
                emitter.onError(e)
            }
        }
    }

    override fun getEntries(): Single<List<StorageEntry>> {
        return Single.create<List<StorageEntry>> { emitter ->
            try {
                val list = storage.getEntries()
                emitter.onSuccess(list)
            } catch (e: Exception) {
                emitter.onError(e)
            }
        }
    }

    override fun removeAll(): Completable {
        return Completable.create { emitter ->
            try {
                storage.removeAll()
                emitter.onComplete()
            } catch (e: Exception) {
                emitter.onError(e)
            }
        }
    }

}