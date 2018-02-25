package com.example.alexeyglushkov.taskmanager.file;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

import android.content.Context;

import com.example.alexeyglushkov.streamlib.data_readers_and_writers.OutputStreamDataWriter;
import com.example.alexeyglushkov.streamlib.data_readers_and_writers.OutputStreamDataWriters;
import com.example.alexeyglushkov.taskmanager.task.SimpleTask;

public class FileKeepTask extends SimpleTask {
    private Context context;
    private String fileName;
    private OutputStreamDataWriter writer;
    private Object data;

    public FileKeepTask(String fileName, OutputStreamDataWriter writer, Object data, Context context) {
        super();
        this.context = context;
        this.fileName = fileName;
        this.writer = writer;
        this.data = data;
    }

    public void startTask(Callback callback) {
        super.startTask(callback);

        if (context != null) {
            String name = this.fileName;
            FileOutputStream fos = null;

            try {
                fos = this.context.openFileOutput(name, Context.MODE_PRIVATE);
                OutputStreamDataWriters.writeOnce(writer, fos, data);

            } catch (Exception e) {
                e.printStackTrace();
                getPrivate().setTaskError(new Error("FileKeepTask exception: " + e.getMessage()));
            }
        } else {
            getPrivate().setTaskError(new Error("Context is null"));
        }

        getPrivate().handleTaskCompletion(callback);
    }
}
