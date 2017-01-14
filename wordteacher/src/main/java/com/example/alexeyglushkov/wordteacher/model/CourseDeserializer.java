package com.example.alexeyglushkov.wordteacher.model;

import com.example.alexeyglushkov.jacksonlib.CustomDeserializer;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

/**
 * Created by alexeyglushkov on 27.03.16.
 */
public class CourseDeserializer extends CustomDeserializer<Course> {
    private static final long serialVersionUID = 5613163670151647085L;

    public CourseDeserializer(Class<Course> vc) {
        super(vc);
    }

    @Override
    protected Course createObject() {
        return new Course();
    }

    @Override
    protected boolean handle(JsonParser p, DeserializationContext ctxt, Course course) throws IOException {
        ObjectMapper mapper = (ObjectMapper) p.getCodec();
        String name = p.getCurrentName();
        boolean isHandled = false;

        if (name.equals("id")) {
            String uuid = _parseString(p, ctxt);
            course.setId(UUID.fromString(uuid));
            isHandled = true;
        } else if (name.equals("title")) {
            course.setTitle(_parseString(p, ctxt));
            isHandled = true;
        } else if (name.equals("createDate")) {
            long createDate = _parseLong(p, ctxt);
            course.setCreateDate(new Date(createDate));
            isHandled = true;
        } else if (name.equals("cards")) {
            Card[] cards = mapper.readValue(p, Card[].class);
            course.setCards(new ArrayList<>(Arrays.asList(cards)));
            isHandled = true;
        }

        return isHandled;
    }
}
