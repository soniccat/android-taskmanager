package com.ga.loader.file;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import android.content.Context;
import com.ga.loader.data.InputStreamHandler;
import com.ga.task.AsyncTask;

public class FileLoadTask extends AsyncTask {
    protected String fileName;
    protected InputStreamHandler handler;
    protected Context context;

    public FileLoadTask(String fileName, InputStreamHandler dataHandler, Context context) {
        super();
        this.context = context;
        this.fileName = fileName;
        this.handler = dataHandler;
    }

    public void setHandler(InputStreamHandler handler) {
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
            fis = new BufferedInputStream(this.context.openFileInput(name));
            setTaskError(handleStream(fis));

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

    protected Error handleStream(InputStream fis) {
        return handler.handleStream(fis);
    }

    protected long getFileSize() {
        File file = new File(context.getFilesDir(), fileName);
        return file.length();
    }

    //old serialization part
    /*
	Object readObject(FileInputStream fis) {
		Object obj = null;
		ObjectInputStream is = null;
		
		try {
			is = new ObjectInputStream(fis);
			obj =  is.readObject();
			
		} catch (Exception ex) {
			this.error = new Error("Load exception");
			
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return obj;
	}
	*/
}
