package com.example.alexeyglushkov.streamlib.readersandwriters;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.example.alexeyglushkov.streamlib.progress.ProgressUpdater;

import java.io.OutputStream;

/**
 * Created by alexeyglushkov on 10.10.15.
 */
public class BitmapWriter implements OutputStreamWriter {
    private Bitmap.CompressFormat format;
    private int quality;

    public BitmapWriter(Bitmap.CompressFormat format, int quality) {
        this.format = format;
        this.quality = quality;
    }

    @Override
    public @NonNull OutputStream wrapStream(@NonNull OutputStream stream) {
        return stream;
    }

    @Override
    public void write(@NonNull OutputStream stream, @NonNull Object object) throws Exception {
        Bitmap bitmap = (Bitmap)object;
        boolean isCompressed = bitmap.compress(format, quality, stream);

        if (!isCompressed) {
            throw new Exception("BitmapWriter write compress error");
        }
    }

    @Override
    public void setProgressUpdater(@NonNull ProgressUpdater progressUpdater) {
    }
}
