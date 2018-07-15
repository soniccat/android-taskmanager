package com.example.alexeyglushkov.streamlib.convertors;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.alexeyglushkov.streamlib.handlers.BitmapHandler;
import com.example.alexeyglushkov.streamlib.handlers.ByteArrayHandler;

/**
 * Created by alexeyglushkov on 01.02.15.
 */
public class BytesBitmapConverter implements ByteArrayHandler<Bitmap> {
    public BytesBitmapConverter() {
    }

    @Override
    public Bitmap convert(byte[] data) {
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }
}
