package com.example.alexeyglushkov.quizletservice.deserializers;

import com.example.alexeyglushkov.jacksonlib.CustomDeserializer;
import com.example.alexeyglushkov.quizletservice.entities.QuizletSet;
import com.example.alexeyglushkov.quizletservice.entities.QuizletTerm;
import com.example.alexeyglushkov.quizletservice.entities.QuizletUser;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by alexeyglushkov on 27.03.16.
 */
public class QuizletSetDeserializer extends CustomDeserializer<QuizletSet> {
    private static final long serialVersionUID = 1600472684113690561L;

    public QuizletSetDeserializer(Class<QuizletSet> vc) {
        super(vc);
    }

    @Override
    protected QuizletSet createObject() {
        return new QuizletSet();
    }

    @Override
    protected boolean handle(JsonParser p, DeserializationContext ctxt, QuizletSet set) throws IOException {
        ObjectMapper mapper = (ObjectMapper) p.getCodec();
        String name = p.getCurrentName();
        boolean isHandled = false;

        if (name.equals("id")) {
            set.setId(_parseLongPrimitive(p, ctxt));
            isHandled = true;
        } else if (name.equals("title")) {
            set.setTitle(_parseString(p, ctxt));
            isHandled = true;
        } else if (name.equals("created_date")) {
            set.setCreateDate(_parseLong(p, ctxt));
            isHandled = true;
        } else if (name.equals("modified_date")) {
            set.setModifiedDate(_parseLong(p, ctxt));
            isHandled = true;
        } else if (name.equals("published_date")) {
            set.setPublishedDate(_parseLong(p, ctxt));
            isHandled = true;
        } else if (name.equals("has_images")) {
            set.setHasImages(_parseBooleanPrimitive(p, ctxt));
            isHandled = true;
        } else if (name.equals("can_edit")) {
            set.setCanEdit(_parseBooleanPrimitive(p, ctxt));
            isHandled = true;
        } else if (name.equals("has_access")) {
            set.setHasAccess(_parseBooleanPrimitive(p, ctxt));
            isHandled = true;
        } else if (name.equals("description")) {
            set.setDescription(_parseString(p, ctxt));
            isHandled = true;
        } else if (name.equals("lang_terms")) {
            set.setLangTerms(_parseString(p, ctxt));
            isHandled = true;
        } else if (name.equals("lang_definitions")) {
            set.setLangDefs(_parseString(p, ctxt));
            isHandled = true;
        } else if (name.equals("creator")) {
            QuizletUser creator = mapper.readValue(p, QuizletUser.class);
            set.setCreator(creator);
            isHandled = true;
        } else if (name.equals("terms")) {
            QuizletTerm[] terms = mapper.readValue(p, QuizletTerm[].class);
            set.setTerms(new ArrayList<>(Arrays.asList(terms)));
            isHandled = true;
        }

        return isHandled;
    }
}
