package com.example.alexeyglushkov.streamlib.readersandwriters;

import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.alexeyglushkov.streamlib.progress.ProgressUpdater;

import java.io.InputStream;

/**
 * Created by alexeyglushkov on 10.10.15.
 */
public class BitmapReader implements InputStreamReader {
    private @Nullable InputStream stream;

    @Override
    public void beginRead(@NonNull InputStream stream) {
        this.stream = stream;
    }

    @Override
    public Object read() {
        return BitmapFactory.decodeStream(stream);
    }

    @Override
    public void closeRead() throws Exception {
    }

    @Override
    public void setProgressUpdater(@NonNull ProgressUpdater progressUpdater) {
        // TODO: investigate
    }
}
