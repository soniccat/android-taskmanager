package com.ga.keeper.file;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;

import android.content.Context;

import com.ga.task.SimpleTask;

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

    public void startTask() {
        if (context == null) {
            return;
        }

        String name = this.fileName;
        FileOutputStream fos = null;

        BufferedOutputStream bufferedStream = null;

        try {
            fos = this.context.openFileOutput(name, Context.MODE_PRIVATE);
            bufferedStream = new BufferedOutputStream(fos);
            setTaskError(writer.writeToStream(bufferedStream));

        } catch (Exception e) {
            e.printStackTrace();
            setTaskError(new Error("FileKeepTask exception: " + e.getMessage()));

        } finally {
            try {
                if (bufferedStream != null) {
                    bufferedStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        getPrivate().handleTaskCompletion();
        return;
    }
}
