package com.example.alexeyglushkov.streamlib.data_readers_and_writers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.alexeyglushkov.streamlib.progress.ProgressUpdater;
import com.example.alexeyglushkov.tools.ExceptionTools;

import java.io.BufferedInputStream;
import java.io.InputStream;

/**
 * Created by alexeyglushkov on 10.10.15.
 */
public class BitmapReader implements InputStreamDataReader<Bitmap> {
    private @Nullable InputStream stream;

    @Override
    public void beginRead(@NonNull InputStream stream) {
        ExceptionTools.throwIfNull(stream, "BitmapReader.beginRead: stream is null");
        this.stream = new BufferedInputStream(stream);
    }

    @Override
    public Bitmap read() {
        ExceptionTools.throwIfNull(stream, "BitmapReader.read: stream is null");
        return BitmapFactory.decodeStream(stream);
    }

    @Override
    public void closeRead() throws Exception {
    }

    @Override
    public void setProgressUpdater(@NonNull ProgressUpdater progressUpdater) {
    }
}
