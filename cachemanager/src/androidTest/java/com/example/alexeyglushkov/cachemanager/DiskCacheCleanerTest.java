package com.example.alexeyglushkov.cachemanager;

import android.content.Context;
import android.test.InstrumentationTestCase;

import java.io.File;

/**
 * Created by alexeyglushkov on 10.10.15.
 */
public class DiskCacheCleanerTest extends InstrumentationTestCase {
    DiskCacheProvider cacheProvider;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        File testDir = getInstrumentation().getContext().getDir("testDir", Context.MODE_PRIVATE);
        cacheProvider = new DiskCacheProvider(testDir);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        cacheProvider.removeAll();
        assertEquals(0, cacheProvider.getEntries().size());
    }

    public void testClean() {
        createEntry(cacheProvider, "1", "123", 0);
        createEntry(cacheProvider, "2", "456", 10);
        createEntry(cacheProvider, "3", "789", -10);

        assertEquals(3, cacheProvider.getEntries().size());

        DiskCacheCleaner cleaner = new DiskCacheCleaner();
        cleaner.clean(cacheProvider);

        assertEquals(1, cacheProvider.getEntries().size());
    }

    private void createEntry(DiskCacheProvider cacheProvider, String key, String value, long timeOffset) {
        DiskCacheMetadata metadata = new DiskCacheMetadata();
        metadata.setExpireTime(System.currentTimeMillis() / 1000L + timeOffset);
        Error error = cacheProvider.put(key, value, metadata);
        assertNull(error);
    }
}
