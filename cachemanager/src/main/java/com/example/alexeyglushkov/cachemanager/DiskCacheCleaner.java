package com.example.alexeyglushkov.cachemanager;

import com.example.alexeyglushkov.streamlib.progress.ProgressUpdater;

import java.util.List;

/**
 * Created by alexeyglushkov on 10.10.15.
 */

// TODO: make universal, remove dependency on a particular cache provider
public class DiskCacheCleaner implements CacheCleaner {
    @Override
    public void setProgressInfo(ProgressUpdater info) {

    }

    @Override
    public void clean(CacheProvider provider) {
        List<CacheEntry> entries = provider.getEntries();
        long currentTime = System.currentTimeMillis() / 1000L;

        for (CacheEntry entry : entries) {
            DiskCacheEntry diskCacheEntry = (DiskCacheEntry)entry;
            DiskCacheMetadata diskCacheMetadata = (DiskCacheMetadata)diskCacheEntry.getMetadata();

            if (currentTime >= diskCacheMetadata.getExpireTime()) {
                entry.delete();
            }
        }
    }
}
