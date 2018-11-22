package com.example.alexeyglushkov.quizletservice.tasks;

import com.example.alexeyglushkov.authorization.requestbuilder.HttpUrlConnectionBuilder;
import com.example.alexeyglushkov.authtaskmanager.HttpServiceCommand;
import com.example.alexeyglushkov.quizletservice.entities.QuizletSet;
import com.example.alexeyglushkov.quizletservice.entities.QuizletTerm;
import com.example.alexeyglushkov.quizletservice.entities.QuizletUser;
import com.example.alexeyglushkov.quizletservice.deserializers.QuizletSetDeserializer;
import com.example.alexeyglushkov.quizletservice.deserializers.QuizletTermDeserializer;
import com.example.alexeyglushkov.quizletservice.deserializers.QuizletUserDeserializer;
import com.example.alexeyglushkov.streamlib.handlers.ByteArrayHandler;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by alexeyglushkov on 03.04.16.
 */
public class QuizletSetsCommand extends HttpServiceCommand<QuizletSet[]>
        implements com.example.alexeyglushkov.quizletservice.QuizletSetsCommand {
    public QuizletSetsCommand(String server, String userId) {
        super(createBuilder(server, userId), createHandler());
    }

    static private HttpUrlConnectionBuilder createBuilder(String server, String userId) {
        HttpUrlConnectionBuilder builder = new HttpUrlConnectionBuilder();
        String url = server + "/users/" + userId + "/sets";
        builder.setUrl(url);
        return builder;
    }

    private static ByteArrayHandler<QuizletSet[]> createHandler() {
        return new ByteArrayHandler<QuizletSet[]>() {
            @Override
            public QuizletSet[] convert(byte[] bytes) {
                return parseSets(bytes);
            }
        };
    }

    static private QuizletSet[] parseSets(byte[] bytes) {
        QuizletSet[] result = null;
        try {
            SimpleModule md = new SimpleModule("QuizletModule", new Version(1,0,0,null,null,null));
            md.addDeserializer(QuizletSet.class, new QuizletSetDeserializer(QuizletSet.class));
            md.addDeserializer(QuizletUser.class, new QuizletUserDeserializer(QuizletUser.class));
            md.addDeserializer(QuizletTerm.class, new QuizletTermDeserializer(QuizletTerm.class));

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(md);

            result = mapper.readValue(bytes, QuizletSet[].class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}
