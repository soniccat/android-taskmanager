package com.example.alexeyglushkov.cachemanager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.test.InstrumentationTestCase;

import com.example.alexeyglushkov.streamlib.serializers.BitmapSerializer;

import java.io.File;

/**
 * Created by alexeyglushkov on 26.09.15.
 */
public class DiskCacheProviderTest extends InstrumentationTestCase {

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

    public void testStoreData() {
        // Arrange
        DiskCacheMetadata metadata = new DiskCacheMetadata();
        metadata.put("mkey", "mvalue");

        // Act
        Error putError = cacheProvider.put("1", "123", metadata);

        // Verify
        assertNull(putError);
        assertEquals("123", cacheProvider.getValue("1"));

        DiskCacheMetadata readMetadata = (DiskCacheMetadata)cacheProvider.getMetadata("1");
        assertNotNull(cacheProvider.getEntry("1"));
        assertEquals("mvalue", readMetadata.get("mkey"));
        assertNotNull(readMetadata.getCreateTime());
        assertTrue(readMetadata.getFileSize() != 0);
    }

    public void testStoreImage() {
        // Arrange
        Bitmap bitmap = BitmapFactory.decodeResource(getInstrumentation().getContext().getResources(),
                R.drawable.imgtocache);
        cacheProvider.setSerializer(new BitmapSerializer(), Bitmap.class);

        // Act
        Error putError = cacheProvider.put("img", bitmap, null);

        // Verify
        assertNull(putError);
        assertNotNull(cacheProvider.getEntry("img"));

        Bitmap result = (Bitmap)cacheProvider.getValue("img");
        DiskCacheMetadata readMetadata = (DiskCacheMetadata)cacheProvider.getMetadata("img");
        assertNotNull(result);
        assertNotNull(readMetadata);
    }

    public void testDeleteEntry() {
        // Act
        cacheProvider.put("key1","a",null);
        cacheProvider.put("key2", "b", null);
        cacheProvider.put("key3", "c", null);

        Error error = cacheProvider.remove("key2");

        // Verify
        assertNull(error);
        assertEquals(2, cacheProvider.getEntries().size());
        assertNull(cacheProvider.getEntry("key2"));
    }
}
