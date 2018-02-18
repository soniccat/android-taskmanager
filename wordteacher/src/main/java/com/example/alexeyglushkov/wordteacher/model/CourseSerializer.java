package com.example.alexeyglushkov.wordteacher.model;

import android.support.annotation.NonNull;

import com.example.alexeyglushkov.streamlib.readersandwriters.InputStreamReader;
import com.example.alexeyglushkov.streamlib.readersandwriters.OutputStreamWriter;
import com.example.alexeyglushkov.streamlib.serializers.Serializer;
import com.example.alexeyglushkov.streamlib.serializers.SimpleSerializer;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by alexeyglushkov on 08.05.16.
 */
public class CourseSerializer extends SimpleSerializer {
    public CourseSerializer() {
        super(new CourseWriter(), new CourseReader());
    }
}
