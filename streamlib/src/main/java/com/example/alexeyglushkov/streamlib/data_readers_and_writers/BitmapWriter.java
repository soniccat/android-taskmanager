package com.example.alexeyglushkov.streamlib.data_readers_and_writers;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.alexeyglushkov.streamlib.progress.ProgressUpdater;
import com.example.alexeyglushkov.tools.ExceptionTools;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.OutputStream;

/**
 * Created by alexeyglushkov on 10.10.15.
 */
public class BitmapWriter implements OutputStreamDataWriter {
    private @Nullable OutputStream stream;
    private Bitmap.CompressFormat format;
    private int quality;

    public BitmapWriter(Bitmap.CompressFormat format, int quality) {
        this.format = format;
        this.quality = quality;
    }

    @Override
    public void beginWrite(@NonNull OutputStream stream) {
        ExceptionTools.throwIfNull(stream, "BitmapWriter.beginWrite: stream is null");
        this.stream = new BufferedOutputStream(stream);
    }

    @Override
    public void closeWrite() throws Exception {
        ExceptionTools.throwIfNull(stream, "BitmapWriter.closeWrite: stream is null");
        stream.close();
    }

    @Override
    public void write(@NonNull Object object) throws Exception {
        ExceptionTools.throwIfNull(stream, "BitmapWriter.write: stream is null");

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
