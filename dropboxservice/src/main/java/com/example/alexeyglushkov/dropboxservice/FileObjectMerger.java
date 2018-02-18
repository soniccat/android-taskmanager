package com.example.alexeyglushkov.dropboxservice;

import com.example.alexeyglushkov.streamlib.serializers.Serializer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by alexeyglushkov on 11.09.16.
 */
public class FileObjectMerger implements FileMerger {
    private Serializer serializer;
    private ObjectMerger objectMerger;
    private File outFile;

    public FileObjectMerger(Serializer serializer, ObjectMerger objectMerger, File outFile) {
        this.serializer = serializer;
        this.objectMerger = objectMerger;
        this.outFile = outFile;
    }

    @Override
    public File merge(File f1, File f2) throws Exception {
        try {
            Object obj1 = readObject(f1);
            Object obj2 = readObject(f2);

            Object resultObj = objectMerger.merge(obj1, obj2);
            writeObject(outFile, resultObj);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return outFile;
    }

    private Object readObject(File f1) throws Exception {
        Object obj = null;

        InputStream fis = null;
        try {
            fis = new BufferedInputStream(new FileInputStream(f1));
            obj = this.serializer.read(fis);

        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return obj;
    }

    private void writeObject(File file, Object obj) throws Exception {
        OutputStream os = null;

        try {
            os = new BufferedOutputStream(new FileOutputStream(file));
            serializer.write(os, obj);

        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
