package com.example.alexeyglushkov.streamlib;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by alexeyglushkov on 27.09.15.
 */
public interface OutputStreamWriter {
    Error writeStream(OutputStream stream, Object object);
    void setProgressUpdater(ProgressUpdater progressUpdater);
    Error getError();
}
