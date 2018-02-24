package com.example.alexeyglushkov.taskmanager.loader.file;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import android.content.Context;

import com.example.alexeyglushkov.streamlib.readersandwriters.InputStreamReader;
import com.example.alexeyglushkov.taskmanager.task.SimpleTask;

public class FileLoadTask extends SimpleTask {
    protected String fileName;
    protected InputStreamReader handler;
    protected Context context;

    public FileLoadTask(String fileName, InputStreamReader dataHandler, Context context) {
        super();
        this.context = context;
        this.fileName = fileName;
        this.handler = dataHandler;
    }

    public void setHandler(InputStreamReader handler) {
        this.handler = handler;
    }

    public void startTask(Callback callback) {
        super.startTask(callback);

        if (context != null) {
            String name = this.fileName;
            InputStream fis = null;

            try {
                // TODO: use InputStreamReaders.readOnce
                // TODO: create FileReader
                handler.setProgressUpdater(getPrivate().createProgressUpdater(getFileSize()));

                fis = new BufferedInputStream(this.context.openFileInput(name));
                Object data = handleStream(fis);
                if (data instanceof Error) {
                    getPrivate().setTaskError((Error) data);
                } else {
                    setTaskResult(data);
                }

            } catch (Exception e) {
                getPrivate().setTaskError(new Error("Load exception"));

            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        } else {
            getPrivate().setTaskError(new Error("Context is null"));
        }

        getPrivate().handleTaskCompletion(callback);
    }

    protected Object handleStream(InputStream fis) throws Exception {
        return handler.read();
    }

    protected long getFileSize() {
        File file = new File(context.getFilesDir(), fileName);
        return file.length();
    }
}
