package com.example.alexeyglushkov.cachemanager;

import com.example.alexeyglushkov.streamlib.progress.ProgressUpdater;

import java.util.List;

/**
 * Created by alexeyglushkov on 10.10.15.
 */

// TODO: make universal, remove dependency on a particular cache provider
public class DiskStorageCleaner implements StorageCleaner {
    @Override
    public void setProgressInfo(ProgressUpdater info) {

    }

    @Override
    public void clean(StorageProvider provider) {
        List<StorageEntry> entries = provider.getEntries();
        long currentTime = System.currentTimeMillis() / 1000L;

        for (StorageEntry entry : entries) {
            DiskStorageEntry diskCacheEntry = (DiskStorageEntry)entry;
            DiskStorageMetadata diskCacheMetadata = (DiskStorageMetadata)diskCacheEntry.getMetadata();

            if (currentTime >= diskCacheMetadata.getExpireTime()) {
                entry.delete();
            }
        }
    }
}
