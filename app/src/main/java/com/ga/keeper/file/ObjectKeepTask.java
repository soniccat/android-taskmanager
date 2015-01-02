package com.ga.keeper.file;

import java.io.FileOutputStream;

import org.apache.http.util.ByteArrayBuffer;

import android.content.Context;

import com.ga.keeper.data.DataProvider;
import com.ga.task.AsyncTask;

public class ObjectKeepTask extends AsyncTask {
    Context context;
    String fileName;
    DataProvider dataProvider;

    public ObjectKeepTask(String fileName, DataProvider dataProvider, Context context) {
        super();
        this.context = context;
        this.fileName = fileName;
        this.dataProvider = dataProvider;
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

            ByteArrayBuffer data = dataProvider.getData();
            fos.write(data.toByteArray());

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

            setTaskError(new Error("Keep exception"));
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        handleTaskCompletion();
        return null;
    }

	/* old stuff
	 * ObjectOutputStream os = null;
	 * os = new ObjectOutputStream(fos);
			os.writeObject(this.keepable);
			if (os != null) {
					os.close(); 
				}
	 * 
	 */
}
