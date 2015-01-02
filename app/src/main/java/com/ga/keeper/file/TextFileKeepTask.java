package com.ga.keeper.file;

import com.ga.task.AsyncTask;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;


public class TextFileKeepTask extends AsyncTask {

    String path;
    String text;

    public TextFileKeepTask(String path, String text) {
        super();
        this.path = path;
        this.text = text;
    }

    @Override
    protected Void doInBackground(Void... params) {

        String path = this.path;
        File f = new File(path);
        String text = this.text;
        this.writeText(f, text);

        return null;
    }

    public void writeText(File f, String text) {
        BufferedWriter bufferedWriter = null;

        try {
            FileOutputStream outputStream = new FileOutputStream(f);
            OutputStreamWriter writer = new OutputStreamWriter(outputStream);
            bufferedWriter = new BufferedWriter(writer);

            bufferedWriter.write(text);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        } finally {
            if (bufferedWriter != null) {
                try {
                    bufferedWriter.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

    }
}
