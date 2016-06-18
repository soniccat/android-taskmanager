package com.example.alexeyglushkov.quizletservice.tasks;

import android.text.format.Time;

import com.example.alexeyglushkov.authorization.requestbuilder.HttpUrlConnectionBuilder;
import com.example.alexeyglushkov.authtaskmanager.ServiceTask;
import com.example.alexeyglushkov.quizletservice.entities.QuizletSet;
import com.example.alexeyglushkov.quizletservice.QuizletSetsCommand;
import com.example.alexeyglushkov.quizletservice.entities.QuizletTerm;
import com.example.alexeyglushkov.quizletservice.entities.QuizletUser;
import com.example.alexeyglushkov.quizletservice.deserializers.QuizletSetDeserializer;
import com.example.alexeyglushkov.quizletservice.deserializers.QuizletTermDeserializer;
import com.example.alexeyglushkov.quizletservice.deserializers.QuizletUserDeserializer;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;

/**
 * Created by alexeyglushkov on 03.04.16.
 */
public class QuizletSetsTask extends ServiceTask implements QuizletSetsCommand {
    private QuizletSet[] sets;

    public QuizletSetsTask(String server, String userId) {
        super();
        build(server, userId);
    }

    private void build(String server, String userId) {
        HttpUrlConnectionBuilder requestBuilder = new HttpUrlConnectionBuilder();

        String url = server + "/users/" + userId + "/sets";
        requestBuilder.setUrl(url);
        setConnectionBuilder(requestBuilder);
    }

    @Override
    public void setHandledData(Object handledData) {
        super.setHandledData(handledData);
        sets = parseSets(getResponse());
    }

    private QuizletSet[] parseSets(String setsResponse) {
        QuizletSet[] result = null;
        try {
            SimpleModule md = new SimpleModule("QuizletModule", new Version(1,0,0,null,null,null));
            md.addDeserializer(QuizletSet.class, new QuizletSetDeserializer(QuizletSet.class));
            md.addDeserializer(QuizletUser.class, new QuizletUserDeserializer(QuizletUser.class));
            md.addDeserializer(QuizletTerm.class, new QuizletTermDeserializer(QuizletTerm.class));

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(md);

            result = mapper.readValue(setsResponse, QuizletSet[].class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public QuizletSet[] getSets() {
        return sets;
    }
}
