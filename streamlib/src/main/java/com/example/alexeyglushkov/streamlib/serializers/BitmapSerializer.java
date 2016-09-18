package com.example.alexeyglushkov.streamlib.serializers;

import android.graphics.Bitmap;

import com.example.alexeyglushkov.streamlib.readersandwriters.BitmapReader;
import com.example.alexeyglushkov.streamlib.readersandwriters.BitmapWriter;
import com.example.alexeyglushkov.streamlib.readersandwriters.InputStreamReader;
import com.example.alexeyglushkov.streamlib.readersandwriters.OutputStreamWriter;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by alexeyglushkov on 10.10.15.
 */
public class BitmapSerializer implements Serializer {
    private OutputStreamWriter writer = new BitmapWriter(Bitmap.CompressFormat.PNG, 100);
    private InputStreamReader reader = new BitmapReader();

    public BitmapSerializer() {
    }

    @Override
    public void setOutputStreamWriter(OutputStreamWriter writer) {
        this.writer = writer;
    }

    @Override
    public void setInputStreamReader(InputStreamReader reader) {
        this.reader = reader;
    }

    @Override
    public void write(OutputStream outputStream, Object value) throws Exception {
        writer.writeStream(outputStream, value);
    }

    @Override
    public Object read(InputStream inputStream) throws Exception {
        return reader.readStream(inputStream);
    }
}
