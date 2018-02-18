package com.example.alexeyglushkov.streamlib.serializers;

import com.example.alexeyglushkov.streamlib.convertors.Converter;
import com.example.alexeyglushkov.streamlib.readersandwriters.InputStreamReader;
import com.example.alexeyglushkov.streamlib.readersandwriters.ObjectReader;
import com.example.alexeyglushkov.streamlib.readersandwriters.ObjectWriter;
import com.example.alexeyglushkov.streamlib.readersandwriters.OutputStreamWriter;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by alexeyglushkov on 04.10.15.
 */
public class ObjectSerializer extends SimpleSerializer {
    public ObjectSerializer() {
        this(null, null);
    }

    public ObjectSerializer(Converter writerConverter,Converter readerConverter) {
        super(new ObjectWriter(writerConverter), new ObjectReader(readerConverter));
    }
}
