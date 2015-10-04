package com.example.alexeyglushkov.streamlib;

import android.graphics.Bitmap;

/**
 * Created by alexeyglushkov on 01.02.15.
 */
public interface BitmapHandler extends Convertor {
    Object handleBitmap(Bitmap bitmap);
}
