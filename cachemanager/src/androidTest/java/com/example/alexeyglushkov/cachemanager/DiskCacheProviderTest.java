package com.example.alexeyglushkov.cachemanager;

import android.content.Context;
import android.test.AndroidTestCase;
import android.test.InstrumentationTestCase;

import com.example.alexeyglushkov.cachemanager.DiskCacheProvider;
import com.noveogroup.android.cache.io.StringSerializer;

import java.io.File;

/**
 * Created by alexeyglushkov on 26.09.15.
 */
public class DiskCacheProviderTest extends InstrumentationTestCase {

    public void testStoreData() {
        File testDir = getInstrumentation().getContext().getDir("testDir", Context.MODE_PRIVATE);
        DiskCacheProvider<String, String> cacheProvider = new DiskCacheProvider<String, String>(testDir, new StringSerializer(), new StringSerializer());

        cacheProvider.storeData("1", "123");

        assertEquals("123", cacheProvider.readData("1"));
    }
}
