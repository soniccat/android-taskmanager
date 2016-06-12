package com.example.alexeyglushkov.quizletservice.tasks;

import com.example.alexeyglushkov.authtaskmanager.ServiceTaskProvider;
import com.example.alexeyglushkov.cachemanager.CacheProvider;
import com.example.alexeyglushkov.quizletservice.QuizletCommandProvider;
import com.example.alexeyglushkov.quizletservice.QuizletSetsCommand;
import com.example.alexeyglushkov.quizletservice.tasks.QuizletSetsTask;
import com.example.alexeyglushkov.service.CachableHttpLoadTask;

import java.io.Serializable;

/**
 * Created by alexeyglushkov on 03.04.16.
 */
public class QuizletServiceTaskProvider extends ServiceTaskProvider implements QuizletCommandProvider {
    private CacheProvider cacheProvider;

    public QuizletServiceTaskProvider(CacheProvider aCacheProvider) {
        this.cacheProvider = aCacheProvider;
    }

    @Override
    public QuizletSetsCommand getLoadSetsCommand(String server, String userId, CachableHttpLoadTask.CacheMode cacheMode) {
        QuizletSetsTask task = new QuizletSetsTask(server, userId);
        task.setCacheMode(cacheMode);
        task.setCache(cacheProvider);

        return task;
    }
}
