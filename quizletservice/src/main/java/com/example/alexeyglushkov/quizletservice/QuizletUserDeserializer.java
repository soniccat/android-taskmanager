package com.example.alexeyglushkov.quizletservice;

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
public class QuizletUserDeserializer extends StdDeserializer<QuizletUser> {
    private static final long serialVersionUID = 4365096740984693871L;

    public QuizletUserDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public QuizletUser deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        QuizletUser user = new QuizletUser();

        JsonToken currentToken = null;
        while ((currentToken = p.nextValue()) != null) {
            switch (currentToken) {
                case VALUE_NUMBER_INT:
                    if (p.getCurrentName().equals("id")) {
                        user.setId(p.getLongValue());
                    }
                    break;
                case START_OBJECT:
                    readObject(p);
                    break;
            }

            if (currentToken == JsonToken.END_OBJECT) {
                break;
            }
        }

        return user;
    }

    private void readObject(JsonParser p) throws IOException {
        JsonToken currentToken = null;
        int objectIndex = 1;
        while ((currentToken = p.nextValue()) != null && objectIndex > 0) {
            switch (currentToken) {
                case START_OBJECT:
                    ++objectIndex;
                    break;
                case END_OBJECT:
                    --objectIndex;
                    break;
            }
        }
    }
}
