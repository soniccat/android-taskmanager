package com.example.alexeyglushkov.streamlib.readersandwriters;

import com.example.alexeyglushkov.streamlib.progress.ProgressUpdater;

import java.io.OutputStream;

/**
 * Created by alexeyglushkov on 27.09.15.
 */
public interface OutputStreamWriter {
    void writeStream(OutputStream stream, Object object) throws Exception;
    void setProgressUpdater(ProgressUpdater progressUpdater);
}
