package com.example.alexeyglushkov.streamlib;

import android.graphics.Bitmap;

import org.apache.http.util.ByteArrayBuffer;

import java.io.ByteArrayOutputStream;

/**
 * Created by alexeyglushkov on 27.09.15.
 */
public class BitmapBytesConvertor implements BitmapHandler {

    @Override
    public Object convert(Object object) {
        return handleBitmap((Bitmap)object);
    }

    @Override
    public Object handleBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

        byte[] byteArray = stream.toByteArray();
        ByteArrayBuffer result = new ByteArrayBuffer(byteArray.length);
        result.append(byteArray,0,byteArray.length);
        return result;
    }
}
