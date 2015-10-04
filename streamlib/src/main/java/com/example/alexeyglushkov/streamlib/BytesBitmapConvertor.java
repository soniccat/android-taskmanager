package com.example.alexeyglushkov.streamlib;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.apache.http.util.ByteArrayBuffer;

/**
 * Created by alexeyglushkov on 01.02.15.
 */
public class BytesBitmapConvertor implements ByteArrayHandler {
    BitmapHandler bitmapHandler;

    public BytesBitmapConvertor(BitmapHandler bitmapHandler) {
        this.bitmapHandler = bitmapHandler;
    }

    @Override
    public Object handleByteArrayBuffer(ByteArrayBuffer byteArray) {
        Object result = null;
        Bitmap bitmap = bitmapFromByteArray(byteArray);
        if (bitmapHandler != null) {
            result = bitmapHandler.handleBitmap(bitmap);
        } else {
            result = bitmap;
        }

        return result;
    }

    @Override
    public Object convert(Object object) {
        return handleByteArrayBuffer((ByteArrayBuffer)object);
    }

    public static Bitmap bitmapFromByteArray(ByteArrayBuffer data) {
        byte[] imageData = data.toByteArray();
        return BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
    }
}
