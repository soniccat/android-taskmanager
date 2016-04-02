package com.example.alexeyglushkov.quizletservice;

import com.example.alexeyglushkov.jacksonlib.MyDeserializer;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

/**
 * Created by alexeyglushkov on 27.03.16.
 */
public class QuizletUserDeserializer extends MyDeserializer<QuizletUser> {
    private static final long serialVersionUID = 4365096740984693871L;
    private QuizletUser user = new QuizletUser();

    public QuizletUserDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    protected boolean handle(JsonParser p, DeserializationContext ctxt) throws IOException {
        boolean isHandled = false;
        String name = p.getCurrentName();

        if (name.equals("id")) {
            user.setId(_parseLongPrimitive(p, ctxt));
            isHandled = true;
        }

        return isHandled;
    }

    @Override
    protected QuizletUser getResult() {
        return user;
    }
}
