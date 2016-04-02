package com.example.alexeyglushkov.quizletservice;

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
public class QuizletSetDeserializer extends StdDeserializer<QuizletSet> {
    private static final long serialVersionUID = 1600472684113690561L;

    public QuizletSetDeserializer(Class<QuizletSet> vc) {
        super(vc);
    }

    @Override
    public QuizletSet deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        QuizletSet set = new QuizletSet();
        ObjectMapper mapper = (ObjectMapper) p.getCodec();
        while (p.nextValue() != null && p.getCurrentToken() != JsonToken.END_OBJECT) {
            String name = p.getCurrentName();
            if (name.equals("id")) {
               set.setId(_parseLongPrimitive(p, ctxt));

            } else if (name.equals("creator")) {
                QuizletUser creator = mapper.readValue(p, QuizletUser.class);
                set.setCreator(creator);

            } else {
                skipElement(p);
            }
        }

        return set;
    }

    private void skipElement(JsonParser p) throws IOException {
        switch (p.getCurrentToken()) {
            case START_ARRAY:
                skipArray(p);
                break;
            case START_OBJECT:
                skipArray(p);
                break;
        }
    }

    private void skipArray(JsonParser p) throws IOException {
        JsonToken currentToken;
        int arrayIndex = 1;
        while (arrayIndex > 0 && (currentToken = p.nextValue()) != null) {
            switch (currentToken) {
                case START_ARRAY:
                    ++arrayIndex;
                    break;
                case END_ARRAY:
                    --arrayIndex;
                    break;
                case START_OBJECT:
                    skipObject(p);
                    break;
            }
        }
    }

    private void skipObject(JsonParser p) throws IOException {
        JsonToken currentToken;
        int objectIndex = 1;
        while (objectIndex > 0 && (currentToken = p.nextValue()) != null) {
            switch (currentToken) {
                case START_OBJECT:
                    ++objectIndex;
                    break;
                case END_OBJECT:
                    --objectIndex;
                    break;
                case START_ARRAY:
                    skipArray(p);
                    break;
            }
        }
    }
}
