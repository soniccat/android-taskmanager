package com.example.alexeyglushkov.cachemanager;

import android.content.Context;
import android.test.AndroidTestCase;
import android.test.InstrumentationTestCase;

import com.example.alexeyglushkov.cachemanager.DiskCacheProvider;
import com.example.alexeyglushkov.streamlib.ObjectSerializer;
import com.noveogroup.android.cache.io.StringSerializer;

import java.io.File;

/**
 * Created by alexeyglushkov on 26.09.15.
 */
public class DiskCacheProviderTest extends InstrumentationTestCase {

    public void testStoreData() {
        File testDir = getInstrumentation().getContext().getDir("testDir", Context.MODE_PRIVATE);
        DiskCacheProvider cacheProvider = new DiskCacheProvider(testDir, new ObjectSerializer());

        DiskCacheMetadata metadata = new DiskCacheMetadata();
        metadata.put("mkey", "mvalue");
        cacheProvider.put("1", "123", metadata);

        assertEquals("123", cacheProvider.getValue("1"));
        assertEquals("mvalue", ((DiskCacheMetadata)cacheProvider.getMetadata("1")).get("mkey"));
    }
}
