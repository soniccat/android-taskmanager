package com.example.alexeyglushkov.wordteacher.model;

import com.example.alexeyglushkov.jacksonlib.CustomDeserializer;
import com.example.alexeyglushkov.quizletservice.entities.QuizletTerm;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

/**
 * Created by alexeyglushkov on 27.03.16.
 */
public class CardDeserializer extends CustomDeserializer<Card> {
    private static final long serialVersionUID = 8749882940069379270L;

    public CardDeserializer(Class<Card> vc) {
        super(vc);
    }

    @Override
    protected Card createObject() {
        return new Card();
    }

    @Override
    protected boolean handle(JsonParser p, DeserializationContext ctxt, Card card) throws IOException {
        ObjectMapper mapper = (ObjectMapper) p.getCodec();
        String name = p.getCurrentName();
        boolean isHandled = false;

        if (name.equals("id")) {
            String uuid = _parseString(p, ctxt);
            card.setId(UUID.fromString(uuid));
            isHandled = true;
        } else if (name.equals("courseId")) {
            String uuid = _parseString(p, ctxt);
            card.setCourseId(UUID.fromString(uuid));
            isHandled = true;
        } else if (name.equals("term")) {
            card.setTerm(_parseString(p, ctxt));
            isHandled = true;
        } else if (name.equals("definition")) {
            card.setDefinition(_parseString(p, ctxt));
            isHandled = true;
        } else if (name.equals("createDate")) {
            long createDate = _parseLong(p, ctxt);
            card.setCreateDate(new Date(createDate));
            isHandled = true;
        } else if (name.equals("quizletTerm")) {
            QuizletTerm quizletTerm = mapper.readValue(p, QuizletTerm.class);
            card.setQuizletTerm(quizletTerm);
            isHandled = true;
        } else if (name.equals("progress")) {
            CardProgress progress = mapper.readValue(p, CardProgress.class);
            card.setProgress(progress);
            isHandled = true;
        }

        return isHandled;
    }
}
