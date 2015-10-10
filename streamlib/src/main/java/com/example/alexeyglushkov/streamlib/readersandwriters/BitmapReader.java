package com.example.alexeyglushkov.streamlib.readersandwriters;

import android.graphics.BitmapFactory;

import com.example.alexeyglushkov.streamlib.progress.ProgressUpdater;

import java.io.InputStream;

/**
 * Created by alexeyglushkov on 10.10.15.
 */
public class BitmapReader implements InputStreamReader {
    @Override
    public Object readStream(InputStream data) {
        return BitmapFactory.decodeStream(data);
    }

    @Override
    public void setProgressUpdater(ProgressUpdater progressUpdater) {

    }

    @Override
    public Error getError() {
        return null;
    }
}
