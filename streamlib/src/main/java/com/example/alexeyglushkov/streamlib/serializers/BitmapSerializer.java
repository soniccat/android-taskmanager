package com.example.alexeyglushkov.streamlib.serializers;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.example.alexeyglushkov.streamlib.readersandwriters.BitmapReader;
import com.example.alexeyglushkov.streamlib.readersandwriters.BitmapWriter;
import com.example.alexeyglushkov.streamlib.readersandwriters.InputStreamReader;
import com.example.alexeyglushkov.streamlib.readersandwriters.OutputStreamWriter;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by alexeyglushkov on 10.10.15.
 */
public class BitmapSerializer extends SimpleSerializer {
    public BitmapSerializer() {
        this(Bitmap.CompressFormat.PNG, 100);
    }

    public BitmapSerializer(Bitmap.CompressFormat format, int quality) {
        super(new BitmapWriter(format, quality),
                new BitmapReader());
    }
}
