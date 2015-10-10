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

    public void testStoreData() {
        File testDir = getInstrumentation().getContext().getDir("testDir", Context.MODE_PRIVATE);
        DiskCacheProvider cacheProvider = new DiskCacheProvider(testDir);

        DiskCacheMetadata metadata = new DiskCacheMetadata();
        metadata.put("mkey", "mvalue");
        Error putError = cacheProvider.put("1", "123", metadata);

        assertNull(putError);
        assertEquals("123", cacheProvider.getValue("1"));

        DiskCacheMetadata readMetadata = (DiskCacheMetadata)cacheProvider.getMetadata("1");
        assertNotNull(cacheProvider.getEntry("1"));
        assertEquals("mvalue", readMetadata.get("mkey"));
        assertNotNull(readMetadata.getCreateTime());
        assertTrue(readMetadata.getFileSize() != 0);
    }

    public void testStoreImage() {
        Bitmap bitmap = BitmapFactory.decodeResource(getInstrumentation().getContext().getResources(),
                R.drawable.imgtocache);

        File testDir = getInstrumentation().getContext().getDir("imageTestDir", Context.MODE_PRIVATE);
        DiskCacheProvider cacheProvider = new DiskCacheProvider(testDir);
        cacheProvider.setSerializer(new BitmapSerializer(), Bitmap.class);

        Error putError = cacheProvider.put("img", bitmap, null);

        assertNull(putError);
        assertNotNull(cacheProvider.getEntry("img"));

        Bitmap result = (Bitmap)cacheProvider.getValue("img");
        assertNotNull(result);
        assertNotNull(cacheProvider.getMetadata("img"));
    }
}
