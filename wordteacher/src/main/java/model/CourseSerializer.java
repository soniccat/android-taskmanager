package model;

import android.graphics.Bitmap;

import com.example.alexeyglushkov.streamlib.readersandwriters.BitmapReader;
import com.example.alexeyglushkov.streamlib.readersandwriters.BitmapWriter;
import com.example.alexeyglushkov.streamlib.readersandwriters.InputStreamReader;
import com.example.alexeyglushkov.streamlib.readersandwriters.OutputStreamWriter;
import com.example.alexeyglushkov.streamlib.serializers.Serializer;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by alexeyglushkov on 08.05.16.
 */
public class CourseSerializer implements Serializer {

    private OutputStreamWriter writer = new CourseWriter();
    private InputStreamReader reader = new CourseReader();

    @Override
    public void setOutputStreamWriter(OutputStreamWriter writer) {
        this.writer = writer;
    }

    @Override
    public void setInputStreamReader(InputStreamReader reader) {
        this.reader = reader;
    }

    @Override
    public Error write(OutputStream outputStream, Object value) {
        return writer.writeStream(outputStream, value);
    }

    @Override
    public Object read(InputStream inputStream) {
        return reader.readStream(inputStream);
    }

    @Override
    public Error getReadError() {
        return reader.getError();
    }

    @Override
    public Error getWriteError() {
        return writer.getError();
    }
}
