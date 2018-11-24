package com.example.alexeyglushkov.cachemanager;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.Maybe;
import io.reactivex.MaybeEmitter;
import io.reactivex.MaybeOnSubscribe;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;

public class AsyncStorageAdapter implements AsyncStorage {
    private @NonNull Storage storage;

    public AsyncStorageAdapter(Storage storage) {
        this.storage = storage;
    }

    @Override
    public Completable put(@NonNull final String key, @NonNull final Object value, @Nullable final StorageMetadata metadata) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) {
                try {
                    storage.put(key, value, metadata);
                    emitter.onComplete();

                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }

    @Override
    public Maybe<Object> getValue(@NonNull final String key) {
        return Maybe.create(new MaybeOnSubscribe<Object>() {
            @Override
            public void subscribe(MaybeEmitter<Object> emitter) {
                try {
                    Object result = storage.getValue(key);
                    if (result != null) {
                        emitter.onSuccess(result);
                    } else {
                        emitter.onComplete();
                    }
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }

    @Override
    public Maybe<StorageMetadata> getMetadata(@NonNull final String key) {
        return Maybe.create(new MaybeOnSubscribe<StorageMetadata>() {
            @Override
            public void subscribe(MaybeEmitter<StorageMetadata> emitter) {
                try {
                    StorageMetadata metadata = storage.getMetadata(key);
                    if (metadata != null) {
                        emitter.onSuccess(metadata);
                    } else {
                        emitter.onComplete();
                    }
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }

    @Override
    public Completable remove(@NonNull final String key) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) {
                try {
                    storage.remove(key);
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }

    @Override
    public Maybe<StorageEntry> getEntry(@NonNull final String key) {
        return Maybe.create(new MaybeOnSubscribe<StorageEntry>() {
            @Override
            public void subscribe(MaybeEmitter<StorageEntry> emitter) {
                try {
                    StorageEntry entry = storage.getEntry(key);

                    if (entry != null) {
                        emitter.onSuccess(entry);
                    } else {
                        emitter.onComplete();
                    }
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }

    @Override
    public Single<List<StorageEntry>> getEntries() {
        return Single.create(new SingleOnSubscribe<List<StorageEntry>>() {
            @Override
            public void subscribe(SingleEmitter<List<StorageEntry>> emitter) {
                try {
                    List<StorageEntry> list = storage.getEntries();
                    emitter.onSuccess(list);
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }

    @Override
    public Completable removeAll() {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) {
                try {
                    storage.removeAll();
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }
}
