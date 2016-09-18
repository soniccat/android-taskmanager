package model;

import com.example.alexeyglushkov.quizletservice.deserializers.QuizletSetDeserializer;
import com.example.alexeyglushkov.quizletservice.deserializers.QuizletTermDeserializer;
import com.example.alexeyglushkov.quizletservice.deserializers.QuizletUserDeserializer;
import com.example.alexeyglushkov.quizletservice.entities.QuizletSet;
import com.example.alexeyglushkov.quizletservice.entities.QuizletTerm;
import com.example.alexeyglushkov.quizletservice.entities.QuizletUser;
import com.example.alexeyglushkov.streamlib.progress.ProgressUpdater;
import com.example.alexeyglushkov.streamlib.readersandwriters.InputStreamReader;
import com.example.alexeyglushkov.streamlib.readersandwriters.StringReader;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by alexeyglushkov on 08.05.16.
 */
public class CourseReader implements InputStreamReader {
    @Override
    public Object readStream(InputStream data) {

        //StringReader reader = new StringReader(null);
        //String str = (String)reader.readStream(data);

        Object result = null;
        try {
            SimpleModule md = new SimpleModule("CourseModule", new Version(1,0,0,null,null,null));
            md.addDeserializer(Course.class, new CourseDeserializer(Course.class));
            md.addDeserializer(Card.class, new CardDeserializer(Card.class));
            md.addDeserializer(QuizletTerm.class, new QuizletTermDeserializer(QuizletTerm.class));
            md.addDeserializer(CardProgress.class, new CardProgressDeserializer(CardProgress.class));

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(md);

            result = mapper.readValue(data, Course.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public void setProgressUpdater(ProgressUpdater progressUpdater) {

    }

    @Override
    public Error getError() {
        return null;
    }
}
