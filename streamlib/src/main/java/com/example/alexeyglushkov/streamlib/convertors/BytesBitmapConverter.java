package com.example.alexeyglushkov.streamlib.convertors;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.alexeyglushkov.streamlib.handlers.BitmapHandler;
import com.example.alexeyglushkov.streamlib.handlers.ByteArrayHandler;

/**
 * Created by alexeyglushkov on 01.02.15.
 */
public class BytesBitmapConverter implements ByteArrayHandler {
    BitmapHandler bitmapHandler;

    public BytesBitmapConverter(BitmapHandler bitmapHandler) {
        this.bitmapHandler = bitmapHandler;
    }

    @Override
    public Object handleByteArrayBuffer(byte[] byteArray) {
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
        return handleByteArrayBuffer((byte[])object);
    }

    public static Bitmap bitmapFromByteArray(byte[] data) {
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }
}
