package com.example.alexeyglushkov.streamlib.readersandwriters;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.alexeyglushkov.streamlib.progress.ProgressUpdater;

import java.io.OutputStream;

/**
 * Created by alexeyglushkov on 10.10.15.
 */
public class BitmapWriter implements OutputStreamWriter {
    private @Nullable OutputStream stream;
    private Bitmap.CompressFormat format;
    private int quality;

    public BitmapWriter(Bitmap.CompressFormat format, int quality) {
        this.format = format;
        this.quality = quality;
    }

    @Override
    public void beginWrite(@NonNull OutputStream stream) {
        this.stream = stream;
    }

    @Override
    public void closeWrite() throws Exception {
        if (stream != null) {
            stream.close();
        }
    }

    @Override
    public void write(@NonNull Object object) throws Exception {
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
