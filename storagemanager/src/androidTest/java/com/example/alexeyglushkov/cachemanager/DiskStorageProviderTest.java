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
        metadata.put("mkey", "mvalue");

        // Act
        Error putError = cacheProvider.put("1", "123", metadata);

        // Verify
        assertNull(putError);
        assertEquals("123", cacheProvider.getValue("1"));

        DiskStorageMetadata readMetadata = (DiskStorageMetadata)cacheProvider.getMetadata("1");
        assertEquals("mvalue", readMetadata.get("mkey"));
        assertNotNull(readMetadata.getCreateTime());
        assertTrue(readMetadata.getContentSize() != 0);
    }

    public void testStoreUrlData() {
        // Arrange
        DiskStorageMetadata metadata = new DiskStorageMetadata();
        metadata.put("https://api.quizlet.com/2.0/users/alexey_glushkov/sets", "https://api.quizlet.com/2.0/users/alexey_glushkov/sets/data");

        // Act
        Error putError = cacheProvider.put("https://api.quizlet.com/2.0/users/alexey_glushkov/sets2", "https://api.quizlet.com/2.0/users/alexey_glushkov/sets/data2", metadata);

        // Verify
        assertNull(putError);
        assertEquals("https://api.quizlet.com/2.0/users/alexey_glushkov/sets/data2", cacheProvider.getValue("https://api.quizlet.com/2.0/users/alexey_glushkov/sets2"));

        DiskStorageMetadata readMetadata = (DiskStorageMetadata)cacheProvider.getMetadata("https://api.quizlet.com/2.0/users/alexey_glushkov/sets2");
        assertEquals("https://api.quizlet.com/2.0/users/alexey_glushkov/sets/data", readMetadata.get("https://api.quizlet.com/2.0/users/alexey_glushkov/sets"));
        assertNotNull(readMetadata.getCreateTime());
        assertTrue(readMetadata.getContentSize() != 0);
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
        DiskStorageMetadata readMetadata = (DiskStorageMetadata)cacheProvider.getMetadata("img");
        assertNotNull(result);
        assertNotNull(readMetadata);
        assertEquals(bitmap.getByteCount(), result.getByteCount());
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