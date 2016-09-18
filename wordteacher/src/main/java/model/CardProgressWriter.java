package model;

import com.example.alexeyglushkov.quizletservice.entities.QuizletTerm;
import com.fasterxml.jackson.core.JsonGenerator;

import java.io.IOException;
import java.util.Date;

/**
 * Created by alexeyglushkov on 14.05.16.
 */
public class CardProgressWriter {
    public void write(CardProgress progress, JsonGenerator g) throws IOException {
        g.writeNumberField("rightAnswerCount", progress.getRightAnswerCount());
        g.writeNumberField("lastMistakeCount", progress.getLastMistakeCount());

        long time = progress.getLastLessonDate().getTime();
        g.writeNumberField("lastLessonDate", time);
    }
}
