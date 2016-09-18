package com.example.alexeyglushkov.streamlib.readersandwriters;

import com.example.alexeyglushkov.streamlib.progress.ProgressUpdater;

import java.io.InputStream;

/**
 * Created by alexeyglushkov on 25.01.15.
 */
public interface InputStreamReader {
    Object readStream(InputStream data) throws Exception;
    void setProgressUpdater(ProgressUpdater progressUpdater);
}
