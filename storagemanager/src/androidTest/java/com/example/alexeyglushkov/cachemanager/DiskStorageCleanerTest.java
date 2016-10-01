package com.example.alexeyglushkov.cachemanager;

import android.content.Context;
import android.test.InstrumentationTestCase;

import com.example.alexeyglushkov.cachemanager.disk.DiskStorageCleaner;
import com.example.alexeyglushkov.cachemanager.disk.DiskStorageMetadata;
import com.example.alexeyglushkov.cachemanager.disk.DiskStorageProvider;

import java.io.File;

/**
 * Created by alexeyglushkov on 10.10.15.
 */
public class DiskStorageCleanerTest extends InstrumentationTestCase {
    DiskStorageProvider cacheProvider;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        File testDir = getInstrumentation().getContext().getDir("testDir", Context.MODE_PRIVATE);
        cacheProvider = new DiskStorageProvider(testDir);
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

        DiskStorageCleaner cleaner = new DiskStorageCleaner();
        cleaner.clean(cacheProvider);

        assertEquals(1, cacheProvider.getEntries().size());
    }

    private void createEntry(DiskStorageProvider cacheProvider, String key, String value, long timeOffset) {
        DiskStorageMetadata metadata = new DiskStorageMetadata();
        metadata.setExpireTime(System.currentTimeMillis() / 1000L + timeOffset);

        Exception ex = null;
        try {
            cacheProvider.put(key, value, metadata);
        } catch (Exception e) {
            ex = e;
        }
        assertNull(ex);
    }
}
