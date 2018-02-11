package com.example.alexeyglushkov.quizletservice.tasks;

import com.example.alexeyglushkov.authtaskmanager.ServiceTaskProvider;
import com.example.alexeyglushkov.cachemanager.StorageProvider;
import com.example.alexeyglushkov.quizletservice.QuizletCommandProvider;
import com.example.alexeyglushkov.quizletservice.QuizletSetsCommand;
import com.example.alexeyglushkov.service.HttpCacheableTransport;
import com.example.alexeyglushkov.streamlib.progress.ProgressListener;

/**
 * Created by alexeyglushkov on 03.04.16.
 */
public class QuizletServiceTaskProvider extends ServiceTaskProvider implements QuizletCommandProvider {
    private StorageProvider storageProvider;

    public QuizletServiceTaskProvider(StorageProvider aStorageProvider) {
        this.storageProvider = aStorageProvider;
    }

    @Override
    public QuizletSetsCommand getLoadSetsCommand(String server, String userId, HttpCacheableTransport.CacheMode cacheMode, ProgressListener progressListener) {
        QuizletSetsTask task = new QuizletSetsTask(server, userId);
        task.setCacheMode(cacheMode);
        task.setCache(storageProvider);
        task.addTaskProgressListener(progressListener);

        return task;
    }
}
