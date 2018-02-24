package com.example.alexeyglushkov.dropboxservice;

import com.example.alexeyglushkov.streamlib.data_readers_and_writers.InputStreamDataReaders;
import com.example.alexeyglushkov.streamlib.data_readers_and_writers.OutputStreamDataWriters;
import com.example.alexeyglushkov.streamlib.codecs.Codec;

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
    private Codec codec;
    private ObjectMerger objectMerger;
    private File outFile;

    public FileObjectMerger(Codec codec, ObjectMerger objectMerger, File outFile) {
        this.codec = codec;
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
        InputStream stream = new BufferedInputStream(new FileInputStream(f1));
        return InputStreamDataReaders.readOnce(codec, stream);
    }

    private void writeObject(File file, Object obj) throws Exception {
        OutputStream stream = new BufferedOutputStream(new FileOutputStream(file));;
        OutputStreamDataWriters.writeOnce(codec, stream, obj);
    }
}
