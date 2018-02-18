package com.example.alexeyglushkov.cachemanager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.test.InstrumentationRegistry;
import android.support.test.annotation.UiThreadTest;
import android.support.test.rule.UiThreadTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.InstrumentationTestCase;

import com.example.alexeyglushkov.cachemanager.disk.DiskStorageMetadata;
import com.example.alexeyglushkov.cachemanager.disk.DiskStorageProvider;
import com.example.alexeyglushkov.streamlib.serializers.BitmapSerializer;

import junit.framework.Assert;

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
public class DiskStorageProviderTest {
    @Rule
    public UiThreadTestRule rule = new UiThreadTestRule();

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
        cacheProvider.setSerializer(new BitmapSerializer(), Bitmap.class);

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
        cacheProvider.setDefaultSerializer(new BitmapSerializer());

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
