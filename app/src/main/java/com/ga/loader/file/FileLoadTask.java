package com.ga.loader.file;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.apache.http.util.ByteArrayBuffer;

import android.content.Context;

import com.ga.loader.data.DataHandler;
import com.ga.task.AsyncTask;
import com.ga.task.DataFormat;

public class FileLoadTask extends AsyncTask {
    String fileName;
    DataHandler dataHandler;
    Context context;

    public FileLoadTask(String fileName, DataHandler dataHandler, Context context) {
        super();
        this.context = context;
        this.fileName = fileName;
        this.dataHandler = dataHandler;
    }

    @Override
    protected Void doInBackground(Void... params) {
        if (context == null) {
            return null;
        }

        String name = this.fileName;
        FileInputStream fis = null;

        try {
            fis = this.context.openFileInput(name);

            ByteArrayBuffer data = this.readStream(fis);
            setTaskError(dataHandler.handleData(data));

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

    public ByteArrayBuffer readStream(InputStream stream) throws IOException, UnsupportedEncodingException {
        ByteArrayBuffer buffer = new ByteArrayBuffer(1024);
        int nRead;
        byte[] data = new byte[1024];

        while ((nRead = stream.read(data, 0, data.length)) != -1) {
            buffer.append(data, 0, nRead);
        }

        return buffer;
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
