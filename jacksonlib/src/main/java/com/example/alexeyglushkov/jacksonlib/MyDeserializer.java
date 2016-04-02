package com.example.alexeyglushkov.jacksonlib;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

/**
 * Created by alexeyglushkov on 02.04.16.
 */
public abstract class MyDeserializer<T> extends StdDeserializer<T> {
    private static final long serialVersionUID = -7320280068663688141L;

    public MyDeserializer(Class<?> vc) {
        super(vc);
    }

    public MyDeserializer(JavaType valueType) {
        super(valueType);
    }

    public MyDeserializer(StdDeserializer<?> src) {
        super(src);
    }

    @Override
    public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        while (p.nextValue() != null && p.getCurrentToken() != JsonToken.END_OBJECT) {
            boolean isHandled = handle(p, ctxt);
            if (!isHandled) {
                skipElement(p);
            }
        }

        return getResult();
    }

    protected abstract boolean handle(JsonParser p, DeserializationContext ctxt) throws IOException;

    protected abstract T getResult();

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
