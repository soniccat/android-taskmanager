package com.example.alexeyglushkov.quizletservice.tasks;

import com.example.alexeyglushkov.authtaskmanager.ServiceTaskProvider;
import com.example.alexeyglushkov.cachemanager.StorageProvider;
import com.example.alexeyglushkov.quizletservice.QuizletCommandProvider;
import com.example.alexeyglushkov.quizletservice.QuizletSetsCommand;
import com.example.alexeyglushkov.service.CachableHttpLoadTask;

/**
 * Created by alexeyglushkov on 03.04.16.
 */
public class QuizletServiceTaskProvider extends ServiceTaskProvider implements QuizletCommandProvider {
    private StorageProvider storageProvider;

    public QuizletServiceTaskProvider(StorageProvider aStorageProvider) {
        this.storageProvider = aStorageProvider;
    }

    @Override
    public QuizletSetsCommand getLoadSetsCommand(String server, String userId, CachableHttpLoadTask.CacheMode cacheMode) {
        QuizletSetsTask task = new QuizletSetsTask(server, userId);
        task.setCacheMode(cacheMode);
        task.setCache(storageProvider);

        return task;
    }
}
