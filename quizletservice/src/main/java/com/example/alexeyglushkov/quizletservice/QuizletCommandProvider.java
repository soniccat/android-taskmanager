package com.example.alexeyglushkov.quizletservice;

import com.example.alexeyglushkov.authorization.Auth.ServiceCommandProvider;

/**
 * Created by alexeyglushkov on 03.04.16.
 */
public interface QuizletCommandProvider extends ServiceCommandProvider {
    QuizletSetsCommand getLoadSetsCommand(String server, String userId);
}
