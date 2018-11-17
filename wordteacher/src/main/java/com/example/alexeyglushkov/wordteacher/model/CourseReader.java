package com.example.alexeyglushkov.wordteacher.model;

import androidx.annotation.NonNull;

import com.example.alexeyglushkov.quizletservice.deserializers.QuizletTermDeserializer;
import com.example.alexeyglushkov.quizletservice.entities.QuizletTerm;
import com.example.alexeyglushkov.streamlib.progress.ProgressUpdater;
import com.example.alexeyglushkov.streamlib.data_readers_and_writers.InputStreamDataReader;
import com.example.alexeyglushkov.tools.ExceptionTools;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by alexeyglushkov on 08.05.16.
 */
public class CourseReader implements InputStreamDataReader {

    private InputStream stream;

    @Override
    public void beginRead(@NonNull InputStream stream) {
        ExceptionTools.throwIfNull(stream, "CourseReader.beginRead: stream is null");
        this.stream = new BufferedInputStream(stream);
    }

    @Override
    public void closeRead() throws Exception {
        ExceptionTools.throwIfNull(stream, "CourseReader.closeRead: stream is null");
        stream.close();
    }

    @Override
    public Object read() throws IOException {
        ExceptionTools.throwIfNull(stream, "CourseReader.read: stream is null");

        //StringReader reader = new StringReader(null);
        //String str = (String)reader.read(data);

        Object result = null;
        SimpleModule md = new SimpleModule("CourseModule", new Version(1,0,0,null,null,null));
        md.addDeserializer(Course.class, new CourseDeserializer(Course.class));
        md.addDeserializer(Card.class, new CardDeserializer(Card.class));
        md.addDeserializer(QuizletTerm.class, new QuizletTermDeserializer(QuizletTerm.class));
        md.addDeserializer(CardProgress.class, new CardProgressDeserializer(CardProgress.class));

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(md);

        result = mapper.readValue(stream, Course.class);

        return result;
    }

    @Override
    public void setProgressUpdater(@NonNull ProgressUpdater progressUpdater) {

    }
}
