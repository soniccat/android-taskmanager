package com.example.alexeyglushkov.cachemanager.disk;

import com.example.alexeyglushkov.cachemanager.StorageCleaner;
import com.example.alexeyglushkov.cachemanager.StorageEntry;
import com.example.alexeyglushkov.cachemanager.StorageProvider;
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

        for (StorageEntry entry : entries) {
            DiskStorageEntry diskCacheEntry = (DiskStorageEntry)entry;
            DiskStorageMetadata diskCacheMetadata = diskCacheEntry.getMetadata();

            if (diskCacheMetadata != null && diskCacheMetadata.isExpired()) {
                entry.delete();
            }
        }
    }
}
