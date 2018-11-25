package com.example.alexeyglushkov.cachemanager;

import com.example.alexeyglushkov.streamlib.progress.ProgressUpdater;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.functions.Consumer;

/**
 * Created by alexeyglushkov on 10.10.15.
 */

public class SimpleStorageCleaner implements StorageCleaner {
    public SimpleStorageCleaner() {
    }

    @Override
    public void setProgressInfo(ProgressUpdater info) {
    }

    @Override
    public Completable clean(Storage storage) {
        RxStorageAdapter asyncStorage = new RxStorageAdapter(storage);
        return clean(asyncStorage);
    }

    @Override
    public Completable clean(RxStorage rxStorage) {
        return rxStorage.getEntries()
            .doOnSuccess(new Consumer<List<StorageEntry>>() {
                @Override
                public void accept(List<StorageEntry> entries){
                    for (StorageEntry entry : entries) {
                        StorageMetadata metadata = entry.getMetadata();

                        if (metadata != null && metadata.isExpired()) {
                            try {
                                entry.delete();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            })
            .ignoreElement();
    }
}
