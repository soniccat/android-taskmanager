package com.example.alexeyglushkov.quizletservice.tasks;

import com.example.alexeyglushkov.authtaskmanager.ServiceTaskProvider;
import com.example.alexeyglushkov.cachemanager.StorageProvider;
import com.example.alexeyglushkov.quizletservice.QuizletCommandProvider;
import com.example.alexeyglushkov.quizletservice.QuizletSetsCommand;
import com.example.alexeyglushkov.service.StorageProviderClient;
import com.example.alexeyglushkov.streamlib.progress.ProgressListener;

/**
 * Created by alexeyglushkov on 03.04.16.
 */
public class QuizletServiceTaskProvider extends ServiceTaskProvider implements QuizletCommandProvider {
    private StorageProvider storageProvider;

    public QuizletServiceTaskProvider(StorageProvider storage) {
        this.storageProvider = storage;
    }

    @Override
    public QuizletSetsCommand getLoadSetsCommand(String server, String userId, StorageProviderClient.CacheMode cacheMode, ProgressListener progressListener) {
        QuizletSetsTask task = new QuizletSetsTask(server, userId);

        StorageProviderClient storageClient = new StorageProviderClient(storageProvider);
        storageClient.setCacheMode(cacheMode);
        task.setCacheClient(storageClient);

        task.addTaskProgressListener(progressListener);
        return task;
    }
}
