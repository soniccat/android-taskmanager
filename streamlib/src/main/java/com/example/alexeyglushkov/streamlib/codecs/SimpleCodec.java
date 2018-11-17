package com.example.alexeyglushkov.streamlib.codecs;

import com.example.alexeyglushkov.streamlib.progress.ProgressUpdater;
import com.example.alexeyglushkov.streamlib.data_readers_and_writers.InputStreamDataReader;
import com.example.alexeyglushkov.streamlib.data_readers_and_writers.OutputStreamDataWriter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by alexeyglushkov on 04.10.15.
 */
public class SimpleCodec<T> implements Codec<T> {
    @NonNull private OutputStreamDataWriter<T> writer;
    @NonNull private InputStreamDataReader<T> reader;

    public SimpleCodec(@NonNull OutputStreamDataWriter<T> writer, @NonNull InputStreamDataReader<T> reader) {
        this.writer = writer;
        this.reader = reader;
    }

    @Override
    public void beginRead(@NonNull InputStream stream) throws Exception {
        reader.beginRead(stream);
    }

    @Override
    public void beginWrite(@NonNull OutputStream stream) throws Exception {
        writer.beginWrite(stream);
    }

    @Override
    public void write(@NonNull T value) throws Exception {
        writer.write(value);
    }

    @Override
    public @Nullable T read() throws Exception {
        return reader.read();
    }

    @Override
    public void closeWrite() throws Exception {
        writer.closeWrite();
    }

    @Override
    public void closeRead() throws Exception {
        reader.closeRead();
    }

    @Override
    public void setProgressUpdater(@NonNull ProgressUpdater progressUpdater) {
        reader.setProgressUpdater(progressUpdater);
        writer.setProgressUpdater(progressUpdater);
    }
}
