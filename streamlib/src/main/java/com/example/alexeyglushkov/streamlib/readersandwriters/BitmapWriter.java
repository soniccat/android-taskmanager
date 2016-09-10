package com.example.alexeyglushkov.streamlib.readersandwriters;

import android.graphics.Bitmap;

import com.example.alexeyglushkov.streamlib.progress.ProgressUpdater;

import java.io.OutputStream;

/**
 * Created by alexeyglushkov on 10.10.15.
 */
public class BitmapWriter implements OutputStreamWriter {
    Bitmap.CompressFormat format;
    int quality;
    Error lastError = null;

    public BitmapWriter(Bitmap.CompressFormat format, int quality) {
        this.format = format;
        this.quality = quality;
    }

    @Override
    public Error writeStream(OutputStream stream, Object object) {
        lastError = null;

        Bitmap bitmap = (Bitmap)object;
        boolean isCompressed = bitmap.compress(format, quality, stream);

        if (!isCompressed) {
            lastError = new Error("BitmapWriter writeStream compress error");
        }

        return lastError;
    }

    @Override
    public void setProgressUpdater(ProgressUpdater progressUpdater) {

    }

    @Override
    public Error getError() {
        return lastError;
    }
}
