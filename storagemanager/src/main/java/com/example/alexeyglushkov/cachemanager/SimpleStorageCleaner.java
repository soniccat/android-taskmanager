package com.example.alexeyglushkov.cachemanager;

import com.example.alexeyglushkov.cachemanager.StorageCleaner;
import com.example.alexeyglushkov.cachemanager.StorageEntry;
import com.example.alexeyglushkov.cachemanager.Storage;
import com.example.alexeyglushkov.cachemanager.StorageMetadata;
import com.example.alexeyglushkov.streamlib.progress.ProgressUpdater;

import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import io.reactivex.Completable;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.internal.functions.Functions;
import io.reactivex.schedulers.Schedulers;

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
        AsyncStorageAdapter asyncStorage = new AsyncStorageAdapter(storage);
        return asyncStorage.getEntries()
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
