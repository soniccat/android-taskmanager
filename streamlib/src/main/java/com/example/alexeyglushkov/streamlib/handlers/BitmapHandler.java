package com.example.alexeyglushkov.streamlib.handlers;

import android.graphics.Bitmap;

import com.example.alexeyglushkov.streamlib.convertors.Convertor;

/**
 * Created by alexeyglushkov on 01.02.15.
 */
public interface BitmapHandler extends Convertor {
    Object handleBitmap(Bitmap bitmap);
}
