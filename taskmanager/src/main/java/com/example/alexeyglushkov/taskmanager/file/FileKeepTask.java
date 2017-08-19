package com.example.alexeyglushkov.taskmanager.file;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;

import android.content.Context;

import com.example.alexeyglushkov.taskmanager.task.SimpleTask;

public class FileKeepTask extends SimpleTask {
    Context context;

    //TODO: think about path
    String fileName;
    OutputStreamWriter writer;

    public FileKeepTask(String fileName, OutputStreamWriter writer, Context context) {
        super();
        this.context = context;
        this.fileName = fileName;
        this.writer = writer;
    }

    public void startTask(Callback callback) {
        if (context != null) {
            String name = this.fileName;
            FileOutputStream fos = null;

            BufferedOutputStream bufferedStream = null;

            try {
                fos = this.context.openFileOutput(name, Context.MODE_PRIVATE);
                bufferedStream = new BufferedOutputStream(fos);
                getPrivate().setTaskError(writer.writeToStream(bufferedStream));

            } catch (Exception e) {
                e.printStackTrace();
                getPrivate().setTaskError(new Error("FileKeepTask exception: " + e.getMessage()));

            } finally {
                try {
                    if (bufferedStream != null) {
                        bufferedStream.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            getPrivate().setTaskError(new Error("Context is null"));
        }

        getPrivate().handleTaskCompletion(callback);
    }
}
