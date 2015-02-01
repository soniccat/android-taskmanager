package com.ga.loader.file;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import android.content.Context;
import com.ga.loader.data.InputStreamReader;
import com.ga.task.AsyncTask;

public class FileLoadTask extends AsyncTask {
    protected String fileName;
    protected InputStreamReader handler;
    protected Context context;
    protected Object handledData;

    public FileLoadTask(String fileName, InputStreamReader dataHandler, Context context) {
        super();
        this.context = context;
        this.fileName = fileName;
        this.handler = dataHandler;
    }

    public void setHandler(InputStreamReader handler) {
        this.handler = handler;
    }

    @Override
    protected Void doInBackground(Void... params) {
        if (context == null) {
            return null;
        }

        String name = this.fileName;
        InputStream fis = null;

        try {
            handler.setProgressUpdater(createProgressUpdater(getFileSize()));

            fis = new BufferedInputStream(this.context.openFileInput(name));
            Object data = handleStream(fis);
            if (data instanceof Error) {
                setTaskError((Error)data);
            } else {
                setHandledData(data);
            }

        } catch (Exception e) {
            setTaskError(new Error("Load exception"));

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

        handleTaskCompletion();
        return null;
    }

    public Object getHandledData() {
        return handledData;
    }

    public void setHandledData(Object handledData) {
        this.handledData = handledData;
    }

    protected Object handleStream(InputStream fis) {
        return handler.readStream(fis);
    }

    protected long getFileSize() {
        File file = new File(context.getFilesDir(), fileName);
        return file.length();
    }
}
