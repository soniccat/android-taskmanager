package com.example.alexeyglushkov.cachemanager;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.annotation.UiThreadTest;
import android.support.test.runner.AndroidJUnit4;
import android.test.InstrumentationTestCase;

import com.example.alexeyglushkov.cachemanager.disk.DiskStorageCleaner;
import com.example.alexeyglushkov.cachemanager.disk.DiskStorageMetadata;
import com.example.alexeyglushkov.cachemanager.disk.DiskStorageProvider;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

/**
 * Created by alexeyglushkov on 10.10.15.
 */
@RunWith(AndroidJUnit4.class)
public class DiskStorageCleanerTest {
    DiskStorageProvider cacheProvider;

    @Before
    public void setUp() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        File testDir = appContext.getDir("testDir", Context.MODE_PRIVATE);
        cacheProvider = new DiskStorageProvider(testDir);
    }

    @After
    public void tearDown() throws Exception {
        cacheProvider.removeAll();

        Assert.assertEquals(0, cacheProvider.getEntries().size());
    }

    @Test @UiThreadTest
    public void testClean() {
        createEntry(cacheProvider, "1", "123", 0);
        createEntry(cacheProvider, "2", "456", 10);
        createEntry(cacheProvider, "3", "789", -10);

        Assert.assertEquals(3, cacheProvider.getEntries().size());

        DiskStorageCleaner cleaner = new DiskStorageCleaner();
        cleaner.clean(cacheProvider);

        Assert.assertEquals(1, cacheProvider.getEntries().size());
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

        Assert.assertNull(ex);
    }
}
