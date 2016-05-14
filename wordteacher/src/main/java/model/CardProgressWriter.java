package model;

import com.example.alexeyglushkov.quizletservice.entities.QuizletTerm;
import com.fasterxml.jackson.core.JsonGenerator;

import java.io.IOException;

/**
 * Created by alexeyglushkov on 14.05.16.
 */
public class CardProgressWriter {
    public void write(CardProgress progress, JsonGenerator g) throws IOException {
        g.writeNumberField("rightAnswerCount", progress.getRightAnswerCount());
        g.writeNumberField("lastMistakeCount", progress.getLastMistakeCount());
        g.writeNumberField("lastLessonDate", progress.getLastLessonDate().getTime());
    }
}
