package com.example.alexeyglushkov.quizletservice.deserializers;

import com.example.alexeyglushkov.jacksonlib.CustomDeserializer;
import com.example.alexeyglushkov.quizletservice.entities.QuizletTerm;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;

import java.io.IOException;

/**
 * Created by alexeyglushkov on 02.04.16.
 */
public class QuizletTermDeserializer extends CustomDeserializer<QuizletTerm> {
    private static final long serialVersionUID = -6788551863193717342L;

    public QuizletTermDeserializer(Class<?> vc) {
        super(vc);
    }

    protected QuizletTerm createObject() {
        return new QuizletTerm();
    }

    @Override
    protected boolean handle(JsonParser p, DeserializationContext ctxt, QuizletTerm term) throws IOException {
        boolean isHandled = false;
        String name = p.getCurrentName();

        if (name.equals("id")) {
            term.setId(_parseLongPrimitive(p, ctxt));
            isHandled = true;
        } else if (name.equals("term")) {
            term.setTerm(_parseString(p, ctxt));
            isHandled = true;
        } else if (name.equals("definition")) {
            term.setDefinition(_parseString(p, ctxt));
            isHandled = true;
        } else if (name.equals("rank")) {
            term.setRank(_parseIntPrimitive(p, ctxt));
            isHandled = true;
        }

        return isHandled;
    }
}
