package com.example.alexeyglushkov.cachemanager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.test.InstrumentationTestCase;

import com.example.alexeyglushkov.cachemanager.disk.DiskStorageMetadata;
import com.example.alexeyglushkov.cachemanager.disk.DiskStorageProvider;
import com.example.alexeyglushkov.streamlib.serializers.BitmapSerializer;

import java.io.File;

/**
 * Created by alexeyglushkov on 26.09.15.
 */
public class DiskStorageProviderTest extends InstrumentationTestCase {

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

    public void testStoreData() {
        // Arrange
        DiskStorageMetadata metadata = new DiskStorageMetadata();
        metadata.setExpireTime(555334);
        metadata.setContentSize(555);

        // Act
        Exception ex = null;
        try {
            cacheProvider.put("1", "123", metadata);
        } catch (Exception e) {
            ex = e;
        }

        // Verify
        assertNull(ex);
        assertEquals("123", cacheProvider.getValue("1"));

        DiskStorageMetadata readMetadata = (DiskStorageMetadata)cacheProvider.getMetadata("1");
        assertNotNull(readMetadata.getCreateTime());
        assertEquals(555334, readMetadata.getExpireTime());
        assertEquals(10, readMetadata.getContentSize());
        assertEquals(String.class, readMetadata.getEntryClass());
        assertTrue(readMetadata.getContentSize() != 0);
    }

    public void testStoreImage() {
        // Arrange
        Bitmap bitmap = BitmapFactory.decodeResource(getInstrumentation().getContext().getResources(),
                R.drawable.imgtocache);
        cacheProvider.setSerializer(new BitmapSerializer(), Bitmap.class);

        // Act
        Exception ex = null;
        try {
            cacheProvider.put("img", bitmap, new DiskStorageMetadata());
        } catch (Exception e) {
            ex = e;
        }

        // Verify
        assertNull(ex);
        assertNotNull(cacheProvider.getEntry("img"));

        Bitmap result = (Bitmap)cacheProvider.getValue("img");
        DiskStorageMetadata readMetadata = (DiskStorageMetadata)cacheProvider.getMetadata("img");
        assertNotNull(result);
        assertNotNull(readMetadata);
        assertEquals(bitmap.getByteCount(), result.getByteCount());
    }
}
