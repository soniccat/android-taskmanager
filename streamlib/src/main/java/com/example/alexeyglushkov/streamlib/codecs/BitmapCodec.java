package com.example.alexeyglushkov.streamlib.codecs;

import android.graphics.Bitmap;

import com.example.alexeyglushkov.streamlib.data_readers_and_writers.BitmapReader;
import com.example.alexeyglushkov.streamlib.data_readers_and_writers.BitmapWriter;

/**
 * Created by alexeyglushkov on 10.10.15.
 */
public class BitmapCodec extends SimpleCodec<Bitmap> {
    public BitmapCodec() {
        this(Bitmap.CompressFormat.PNG, 100);
    }

    public BitmapCodec(Bitmap.CompressFormat format, int quality) {
        super(new BitmapWriter(format, quality),
                new BitmapReader());
    }
}
