package com.ga.loader.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import com.ga.loader.data.StringHandler;
import com.ga.task.AsyncTask;

public class TextFileLoadTask extends AsyncTask {

    String path;
    StringHandler stringHandler;

    public TextFileLoadTask(String path, StringHandler stringHandler) {
        super();

        this.path = path;
        this.stringHandler = stringHandler;
    }

    @Override
    protected Void doInBackground(Void... params) {
        String path = this.path;
        File f = new File(path);
        String text = this.readFile(f);

        if (text != null) {
            this.stringHandler.handleString(text);
        }

        return null;
    }

    public String readFile(File file) {
        StringBuffer datax = new StringBuffer("");
        BufferedReader buffreader = null;
        try {
            FileInputStream fIn = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fIn);
            buffreader = new BufferedReader(isr);

            String readString = buffreader.readLine();
            while (readString != null) {
                datax.append(readString);
                readString = buffreader.readLine();
            }

        } catch (IOException ioe) {
            ioe.printStackTrace();

        } finally {
            if (buffreader != null) {
                try {
                    buffreader.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return datax.toString();
    }
}
