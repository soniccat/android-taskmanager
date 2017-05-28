package com.example.alexeyglushkov.streamlib.readersandwriters;

import com.example.alexeyglushkov.streamlib.convertors.Convertor;
import com.example.alexeyglushkov.streamlib.progress.ProgressUpdater;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by alexeyglushkov on 27.09.15.
 */
public class ByteArrayWriter implements OutputStreamWriter {
    private Convertor convertor;

    public ByteArrayWriter(Convertor convertor) {
        this.convertor = convertor;
    }

    @Override
    public void writeStream(OutputStream stream, Object object) throws Exception {
        if (convertor != null) {
            object = this.convertor.convert(object);
        }

        byte[] buffer = (byte[])object;
        writeByteArray(stream, buffer);
    }

    public void writeByteArray(OutputStream stream, byte[] buffer) throws IOException {
        stream.write(buffer);
    }

    @Override
    public void setProgressUpdater(ProgressUpdater progressUpdater) {

    }
}
