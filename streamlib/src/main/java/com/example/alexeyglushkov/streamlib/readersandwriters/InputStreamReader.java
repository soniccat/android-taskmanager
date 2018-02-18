package com.example.alexeyglushkov.streamlib.readersandwriters;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.alexeyglushkov.streamlib.progress.ProgressUpdater;

import java.io.InputStream;

/**
 * Created by alexeyglushkov on 25.01.15.
 */
public interface InputStreamReader {
    InputStream wrapInputStream(@NonNull InputStream stream);
    @Nullable Object readStream(@NonNull InputStream data) throws Exception;
    void setProgressUpdater(@NonNull ProgressUpdater progressUpdater);
}
