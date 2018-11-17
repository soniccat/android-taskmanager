package com.example.alexeyglushkov.cachemanager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.test.InstrumentationRegistry;
import androidx.test.annotation.UiThreadTest;
import androidx.test.rule.UiThreadTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.example.alexeyglushkov.cachemanager.disk.DiskStorageMetadata;
import com.example.alexeyglushkov.cachemanager.disk.DiskStorage;
import com.example.alexeyglushkov.streamlib.codecs.BitmapCodec;

import org.junit.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

/**
 * Created by alexeyglushkov on 26.09.15.
 */
@RunWith(AndroidJUnit4.class)
public class DiskStorageTest {
    @Rule
    public UiThreadTestRule rule = new UiThreadTestRule();

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
    public void testGetDirectory() {
        Assert.assertTrue(cacheProvider.getDirectory().getPath().endsWith("testDir"));
    }

    @Test @UiThreadTest
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
        Assert.assertNull(ex);
        Assert.assertEquals("123", cacheProvider.getValue("1"));

        DiskStorageMetadata readMetadata = (DiskStorageMetadata)cacheProvider.getMetadata("1");
        Assert.assertNotNull(readMetadata.getCreateTime());
        Assert.assertEquals(555334, readMetadata.getExpireTime());
        Assert.assertEquals(10, readMetadata.getContentSize());
        Assert.assertEquals(String.class, readMetadata.getEntryClass());
        Assert.assertTrue(readMetadata.getContentSize() != 0);
    }

    @Test @UiThreadTest
    public void testStoreImage() {
        // Arrange
        Context appContext = InstrumentationRegistry.getTargetContext();
        Bitmap bitmap = BitmapFactory.decodeResource(appContext.getResources(),
                R.drawable.imgtocache);
        cacheProvider.setSerializer(new BitmapCodec(), Bitmap.class);

        // Act
        Exception ex = null;
        try {
            cacheProvider.put("img", bitmap, new DiskStorageMetadata());
        } catch (Exception e) {
            ex = e;
        }

        // Verify
        Assert.assertNull(ex);
        Assert.assertNotNull(cacheProvider.getEntry("img"));

        Bitmap result = (Bitmap)cacheProvider.getValue("img");
        DiskStorageMetadata readMetadata = (DiskStorageMetadata)cacheProvider.getMetadata("img");
        Assert.assertNotNull(result);
        Assert.assertNotNull(readMetadata);
        Assert.assertEquals(bitmap.getByteCount(), result.getByteCount());
    }

    @Test @UiThreadTest
    public void testEntryCount() {
        // Act
        Exception ex = null;
        try {
            cacheProvider.put("1", "123", null);
            cacheProvider.put("2", "543", null);
            cacheProvider.put("3", "1462", null);
        } catch (Exception e) {
            ex = e;
        }

        // Verify
        Assert.assertNull(ex);
        Assert.assertEquals(3, cacheProvider.getEntryCount());
    }

    @Test @UiThreadTest
    public void testRemove() {
        // Act
        Exception ex = null;
        try {
            cacheProvider.put("1", "123", null);
            cacheProvider.put("2", "543", null);
            cacheProvider.put("3", "1462", null);

            cacheProvider.remove("2");
        } catch (Exception e) {
            ex = e;
        }

        // Verify
        Assert.assertNull(ex);
        Assert.assertEquals(2, cacheProvider.getEntryCount());
        Assert.assertNull(cacheProvider.getEntry("2"));
    }

    @Test @UiThreadTest
    public void testCreateMetadata() {
        DiskStorageMetadata metadata = cacheProvider.createMetadata();
        Assert.assertEquals(DiskStorageMetadata.class, metadata.getClass());
    }

    @Test @UiThreadTest
    public void testSetDefaultSerializer() {
        // Act
        Context appContext = InstrumentationRegistry.getTargetContext();
        cacheProvider.setDefaultCodec(new BitmapCodec());

        Bitmap bitmap = BitmapFactory.decodeResource(appContext.getResources(),
                R.drawable.imgtocache);

        // Act
        Exception ex = null;
        try {
            cacheProvider.put("img", bitmap, null);
        } catch (Exception e) {
            ex = e;
        }

        // Verify
        Assert.assertNull(ex);

        Bitmap result = (Bitmap)cacheProvider.getValue("img");
        Assert.assertNotNull(result);
        Assert.assertEquals(bitmap.getByteCount(), result.getByteCount());
    }
}
