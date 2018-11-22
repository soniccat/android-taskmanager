package com.example.alexeyglushkov.quizletservice.tasks;

import com.example.alexeyglushkov.authtaskmanager.HttpServiceCommand;
import com.example.alexeyglushkov.quizletservice.entities.QuizletSet;
import com.example.alexeyglushkov.quizletservice.entities.QuizletTerm;
import com.example.alexeyglushkov.quizletservice.entities.QuizletUser;
import com.example.alexeyglushkov.quizletservice.deserializers.QuizletSetDeserializer;
import com.example.alexeyglushkov.quizletservice.deserializers.QuizletTermDeserializer;
import com.example.alexeyglushkov.quizletservice.deserializers.QuizletUserDeserializer;
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
public class QuizletSetsCommand extends HttpServiceCommand implements com.example.alexeyglushkov.quizletservice.QuizletSetsCommand {
    private List<QuizletSet> sets;

    public QuizletSetsCommand(String server, String userId) {
        super();
        build(server, userId);
    }

    private void build(String server, String userId) {
        String url = server + "/users/" + userId + "/sets";
        getConnectionBuilder().setUrl(url);
    }

    @Override
    public void setTaskResult(Object handledData) {
        super.setTaskResult(handledData);
        QuizletSet[] setArray = parseSets(getResponse());
        sets = new ArrayList<>(Arrays.asList(setArray));
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
    public List<QuizletSet> getSets() {
        return sets;
    }
}
