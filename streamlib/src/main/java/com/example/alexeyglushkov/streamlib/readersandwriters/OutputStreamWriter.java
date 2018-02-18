package com.example.alexeyglushkov.streamlib.readersandwriters;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.alexeyglushkov.streamlib.progress.ProgressUpdater;

import java.io.OutputStream;

/**
 * Created by alexeyglushkov on 27.09.15.
 */
public interface OutputStreamWriter {
    @NonNull OutputStream wrapOutputStream(@NonNull OutputStream stream) throws Exception;
    void writeStream(@NonNull OutputStream stream, @NonNull Object object) throws Exception;
    void setProgressUpdater(@NonNull ProgressUpdater progressUpdater);
}
