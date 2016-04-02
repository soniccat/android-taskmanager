package com.example.alexeyglushkov.quizletservice;

import com.example.alexeyglushkov.authorization.Auth.ServiceCommand;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommandProvider;
import com.example.alexeyglushkov.authtaskmanager.ServiceTaskProvider;

/**
 * Created by alexeyglushkov on 03.04.16.
 */
public class QuizletServiceTaskProvider extends ServiceTaskProvider implements QuizletCommandProvider {
    @Override
    public ServiceCommand getLoadSetsCommand(String server, String userId) {
        return new QuizletSetsCommand(server, userId);
    }
}
