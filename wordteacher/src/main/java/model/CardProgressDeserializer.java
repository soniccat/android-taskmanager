package model;

import com.example.alexeyglushkov.jacksonlib.CustomDeserializer;
import com.example.alexeyglushkov.quizletservice.entities.QuizletTerm;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

/**
 * Created by alexeyglushkov on 14.05.16.
 */
public class CardProgressDeserializer extends CustomDeserializer<CardProgress> {
    private static final long serialVersionUID = -6641302710174171110L;

    public CardProgressDeserializer(Class<CardProgress> vc) {
        super(vc);
    }

    @Override
    protected CardProgress createObject() {
        return new CardProgress();
    }

    @Override
    protected boolean handle(JsonParser p, DeserializationContext ctxt, CardProgress cardProgress) throws IOException {
        String name = p.getCurrentName();
        boolean isHandled = false;

        if (name.equals("rightAnswerCount")) {
            cardProgress.setRightAnswerCount(_parseInteger(p, ctxt));
            isHandled = true;
        } else if (name.equals("lastMistakeCount")) {
            cardProgress.setLastMistakeCount(_parseInteger(p, ctxt));
            isHandled = true;
        } else if (name.equals("lastLessonDate")) {
            long lastLessonDate = _parseLong(p, ctxt);
            cardProgress.setLastLessonDate(new Date(lastLessonDate));
            isHandled = true;
        }

        return isHandled;
    }
}
