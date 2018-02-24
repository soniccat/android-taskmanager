package com.example.alexeyglushkov.streamlib.readersandwriters;

import android.support.annotation.NonNull;

import com.example.alexeyglushkov.streamlib.progress.ProgressUpdater;

import java.io.OutputStream;

/**
 * Created by alexeyglushkov on 27.09.15.
 */
public interface OutputStreamWriter {
    void beginWrite(@NonNull OutputStream stream) throws Exception;
    void write(@NonNull Object object) throws Exception;
    void closeWrite() throws Exception;
    void setProgressUpdater(@NonNull ProgressUpdater progressUpdater);
}
