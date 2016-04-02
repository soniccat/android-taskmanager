package com.example.alexeyglushkov.quizletservice;

import com.example.alexeyglushkov.authorization.Auth.ServiceCommand;
import com.example.alexeyglushkov.authorization.requestbuilder.HttpUrlConnectionBuilder;
import com.example.alexeyglushkov.authtaskmanager.ServiceTask;

/**
 * Created by alexeyglushkov on 03.04.16.
 */
public class QuizletSetsCommand extends ServiceTask {
    public QuizletSetsCommand(String server, String userId) {
        super();
        build(server, userId);
    }

    private void build(String server, String userId) {
        HttpUrlConnectionBuilder requestBuilder = new HttpUrlConnectionBuilder();

        String url = server + "/users/" + userId + "/sets";
        requestBuilder.setUrl(url);
        setConnectionBuilder(requestBuilder);
    }
}
