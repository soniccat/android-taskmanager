package com.example.alexeyglushkov.quizletservice;

import com.example.alexeyglushkov.authorization.Auth.ServiceCommandProvider;
import com.example.alexeyglushkov.cachemanager.clients.Cache;
import com.example.alexeyglushkov.streamlib.progress.ProgressListener;

/**
 * Created by alexeyglushkov on 03.04.16.
 */
public interface QuizletCommandProvider extends ServiceCommandProvider {
    QuizletSetsCommand getLoadSetsCommand(String server, String userId, ProgressListener progressListener);
}
