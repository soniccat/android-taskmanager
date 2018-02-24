package com.example.alexeyglushkov.streamlib.data_readers_and_writers;

import android.support.annotation.NonNull;

import com.example.alexeyglushkov.streamlib.progress.ProgressUpdater;

import java.io.OutputStream;

/**
 * Created by alexeyglushkov on 27.09.15.
 */
public interface OutputStreamDataWriter {
    void beginWrite(@NonNull OutputStream stream) throws Exception;
    void write(@NonNull Object object) throws Exception;
    void closeWrite() throws Exception;
    void setProgressUpdater(@NonNull ProgressUpdater progressUpdater);
}