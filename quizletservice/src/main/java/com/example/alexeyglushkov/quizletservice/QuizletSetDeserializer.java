package com.example.alexeyglushkov.quizletservice;

import com.example.alexeyglushkov.jacksonlib.MyDeserializer;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by alexeyglushkov on 27.03.16.
 */
public class QuizletSetDeserializer extends MyDeserializer<QuizletSet> {
    private static final long serialVersionUID = 1600472684113690561L;
    private QuizletSet set = new QuizletSet();

    public QuizletSetDeserializer(Class<QuizletSet> vc) {
        super(vc);
    }

    @Override
    protected boolean handle(JsonParser p, DeserializationContext ctxt) throws IOException{
        ObjectMapper mapper = (ObjectMapper) p.getCodec();
        String name = p.getCurrentName();
        boolean isHandled = false;

        if (name.equals("id")) {
            set.setId(_parseLongPrimitive(p, ctxt));
            isHandled = true;

        } else if (name.equals("creator")) {
            QuizletUser creator = mapper.readValue(p, QuizletUser.class);
            set.setCreator(creator);
            isHandled = true;
        }

        return isHandled;
    }

    @Override
    protected QuizletSet getResult() {
        return set;
    }
}
