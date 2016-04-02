package com.example.alexeyglushkov.quizletservice;

import com.example.alexeyglushkov.jacksonlib.CustomDeserializer;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;

import java.io.IOException;

/**
 * Created by alexeyglushkov on 27.03.16.
 */
public class QuizletUserDeserializer extends CustomDeserializer<QuizletUser> {
    private static final long serialVersionUID = 4365096740984693871L;

    public QuizletUserDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    protected QuizletUser createObject() {
        return new QuizletUser();
    }

    @Override
    protected boolean handle(JsonParser p, DeserializationContext ctxt, QuizletUser user) throws IOException {
        boolean isHandled = false;
        String name = p.getCurrentName();

        if (name.equals("id")) {
            user.setId(_parseLongPrimitive(p, ctxt));
            isHandled = true;
        } else if (name.equals("username")) {
            user.setName(_parseString(p, ctxt));
            isHandled = true;
        } else if (name.equals("profile_image")) {
            user.setImageUrl(_parseString(p, ctxt));
            isHandled = true;
        } else if (name.equals("account_type")) {
            user.setType(_parseString(p, ctxt));
            isHandled = true;
        }

        return isHandled;
    }
}
