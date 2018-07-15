package com.example.alexeyglushkov.streamlib.data_readers_and_writers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.alexeyglushkov.streamlib.progress.ProgressUpdater;

import java.io.InputStream;

/**
 * Created by alexeyglushkov on 25.01.15.
 */
public interface InputStreamDataReader<T> {
    void beginRead(@NonNull InputStream stream) throws Exception;
    @Nullable T read() throws Exception;
    void closeRead() throws Exception;
    void setProgressUpdater(@NonNull ProgressUpdater progressUpdater);
}
