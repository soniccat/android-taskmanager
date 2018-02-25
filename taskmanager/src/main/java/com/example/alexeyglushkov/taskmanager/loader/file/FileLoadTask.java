package com.example.alexeyglushkov.taskmanager.loader.file;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import android.content.Context;

import com.example.alexeyglushkov.streamlib.data_readers_and_writers.InputStreamDataReader;
import com.example.alexeyglushkov.streamlib.data_readers_and_writers.InputStreamDataReaders;
import com.example.alexeyglushkov.streamlib.data_readers_and_writers.OutputStreamDataWriters;
import com.example.alexeyglushkov.taskmanager.task.SimpleTask;

public class FileLoadTask extends SimpleTask {
    protected String fileName;
    protected InputStreamDataReader reader;
    protected Context context;

    public FileLoadTask(String fileName, InputStreamDataReader reader, Context context) {
        super();
        this.context = context;
        this.fileName = fileName;
        this.reader = reader;
    }

    public void startTask(Callback callback) {
        super.startTask(callback);

        if (context != null) {
            String name = this.fileName;

            try {
                reader.setProgressUpdater(getPrivate().createProgressUpdater(getFileSize()));

                InputStream fis = this.context.openFileInput(name);
                Object data = InputStreamDataReaders.readOnce(reader, fis);
                setTaskResult(data);

            } catch (Exception e) {
                getPrivate().setTaskError(new Error("FileLoadTask exception: " + e.getMessage()));
            }
        } else {
            getPrivate().setTaskError(new Error("Context is null"));
        }

        getPrivate().handleTaskCompletion(callback);
    }

    protected long getFileSize() {
        File file = new File(context.getFilesDir(), fileName);
        return file.length();
    }
}
