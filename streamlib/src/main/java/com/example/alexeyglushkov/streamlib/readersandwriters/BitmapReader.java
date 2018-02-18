package com.example.alexeyglushkov.streamlib.readersandwriters;

import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;

import com.example.alexeyglushkov.streamlib.progress.ProgressUpdater;

import java.io.InputStream;

/**
 * Created by alexeyglushkov on 10.10.15.
 */
public class BitmapReader implements InputStreamReader {
    @Override
    public InputStream wrapInputStream(@NonNull InputStream stream) {
        return stream;
    }

    @Override
    public Object readStream(@NonNull InputStream data) {
        return BitmapFactory.decodeStream(data);
    }

    @Override
    public void setProgressUpdater(@NonNull ProgressUpdater progressUpdater) {
        // TODO: investigate
    }
}
