package com.ga.loader.data;

import com.ga.loader.ProgressUpdater;

import org.apache.http.util.ByteArrayBuffer;

import java.io.InputStream;

/**
 * Created by alexeyglushkov on 25.01.15.
 */
public interface InputStreamReader {
    Object readStream(InputStream data);
    void setProgressUpdater(ProgressUpdater progressUpdater);
}
