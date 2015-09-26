package com.taskmanager.loader.data;

import com.taskmanager.loader.ProgressUpdater;

import java.io.InputStream;

/**
 * Created by alexeyglushkov on 25.01.15.
 */
public interface InputStreamReader {
    Object readStream(InputStream data);
    void setProgressUpdater(ProgressUpdater progressUpdater);
}
