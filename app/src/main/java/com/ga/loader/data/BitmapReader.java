package com.ga.loader.data;

import android.graphics.Bitmap;

import com.ga.image.Image;

import org.apache.http.util.ByteArrayBuffer;

/**
 * Created by alexeyglushkov on 01.02.15.
 */
public class BitmapReader implements ByteArrayHandler {
    BitmapHandler bitmapHandler;

    public BitmapReader(BitmapHandler bitmapHandler) {
        this.bitmapHandler = bitmapHandler;
    }

    @Override
    public Object handleByteArrayBuffer(ByteArrayBuffer byteArray) {
        Object result = null;
        Bitmap bitmap = Image.bitmapFromByteArray(byteArray);
        if (bitmapHandler != null) {
            result = bitmapHandler.handleBitmap(bitmap);
        } else {
            result = bitmap;
        }

        return result;
    }
}
