package com.ga.keeper.file;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;

import android.content.Context;

import com.ga.task.AsyncTask;

public class FileKeepTask extends AsyncTask {
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

    @Override
    protected Void doInBackground(Void... params) {

        if (context == null) {
            return null;
        }

        String name = this.fileName;
        FileOutputStream fos = null;

        try {
            fos = this.context.openFileOutput(name, Context.MODE_PRIVATE);
            BufferedOutputStream bufferedStream = new BufferedOutputStream(fos);
            setTaskError(writer.writeToStream(bufferedStream));

        } catch (Exception e) {
            e.printStackTrace();

            setTaskError(new Error("Keep exception " + e.getMessage()));
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        handleTaskCompletion();
        return null;
    }
}
