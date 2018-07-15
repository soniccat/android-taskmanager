package com.example.alexeyglushkov.streamlib.data_readers_and_writers;

import android.support.annotation.NonNull;

import com.example.alexeyglushkov.streamlib.progress.ProgressUpdater;

import java.io.OutputStream;

/**
 * Created by alexeyglushkov on 27.09.15.
 */
// TODO: think about OutputDataWriter which rely on Object in beginWrite
//       to be able to implement a database writer for example
public interface OutputStreamDataWriter<T> {
    void beginWrite(@NonNull OutputStream stream) throws Exception;
    void write(@NonNull T object) throws Exception;
    void closeWrite() throws Exception;
    void setProgressUpdater(@NonNull ProgressUpdater progressUpdater);
}
