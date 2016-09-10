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
    private Error lastError;

    public ByteArrayWriter(Convertor convertor) {
        this.convertor = convertor;
    }

    @Override
    public Error writeStream(OutputStream stream, Object object) {
        lastError = null;

        if (convertor != null) {
            object = this.convertor.convert(object);
        }

        /*
        ByteArrayBuffer buffer = (ByteArrayBuffer)object;
        try {
            lastError = writeByteArray(stream, buffer);
        } catch (IOException ex) {
            lastError = new Error("ByteArrayWriter exception: " + ex.getMessage());
        }*/

        return lastError;
    }

    /*
    public Error writeByteArray(OutputStream stream, ByteArrayBuffer buffer) throws IOException {
        //stream.write(buffer.buffer());
        return null;
    }*/

    @Override
    public void setProgressUpdater(ProgressUpdater progressUpdater) {

    }

    @Override
    public Error getError() {
        return lastError;
    }
}
