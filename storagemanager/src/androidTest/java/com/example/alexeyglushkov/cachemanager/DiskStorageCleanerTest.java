package com.example.alexeyglushkov.cachemanager;

import android.content.Context;
import androidx.test.InstrumentationRegistry;
import androidx.test.annotation.UiThreadTest;
import androidx.test.runner.AndroidJUnit4;

import com.example.alexeyglushkov.cachemanager.disk.DiskStorageCleaner;
import com.example.alexeyglushkov.cachemanager.disk.DiskStorageMetadata;
import com.example.alexeyglushkov.cachemanager.disk.DiskStorage;
import com.example.alexeyglushkov.tools.TimeTools;

import org.junit.Assert;

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
    DiskStorage cacheProvider;

    @Before
    public void setUp() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        File testDir = appContext.getDir("testDir", Context.MODE_PRIVATE);
        cacheProvider = new DiskStorage(testDir);
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

    private void createEntry(DiskStorage cacheProvider, String key, String value, long timeOffset) {
        DiskStorageMetadata metadata = new DiskStorageMetadata();
        metadata.setExpireTime(TimeTools.currentTimeSeconds() + timeOffset);

        Exception ex = null;
        try {
            cacheProvider.put(key, value, metadata);
        } catch (Exception e) {
            ex = e;
        }

        Assert.assertNull(ex);
    }
}
