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

    public BitmapWriter(Bitmap.CompressFormat format, int quality) {
        this.format = format;
        this.quality = quality;
    }

    @Override
    public void writeStream(OutputStream stream, Object object) throws Exception {
        Bitmap bitmap = (Bitmap)object;
        boolean isCompressed = bitmap.compress(format, quality, stream);

        if (!isCompressed) {
            throw new Exception("BitmapWriter writeStream compress error");
        }
    }

    @Override
    public void setProgressUpdater(ProgressUpdater progressUpdater) {

    }
}
