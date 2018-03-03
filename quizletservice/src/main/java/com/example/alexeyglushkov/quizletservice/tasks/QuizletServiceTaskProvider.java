package com.example.alexeyglushkov.quizletservice.tasks;

import com.example.alexeyglushkov.authtaskmanager.ServiceTaskProvider;
import com.example.alexeyglushkov.quizletservice.QuizletCommandProvider;
import com.example.alexeyglushkov.quizletservice.QuizletSetsCommand;
import com.example.alexeyglushkov.service.StorageProviderClient;
import com.example.alexeyglushkov.streamlib.progress.ProgressListener;

/**
 * Created by alexeyglushkov on 03.04.16.
 */
public class QuizletServiceTaskProvider extends ServiceTaskProvider implements QuizletCommandProvider {
    private StorageProviderClient storageClient;

    public QuizletServiceTaskProvider(StorageProviderClient storageClient) {
        this.storageClient = storageClient;
    }

    @Override
    public QuizletSetsCommand getLoadSetsCommand(String server, String userId, StorageProviderClient.CacheMode cacheMode, ProgressListener progressListener) {
        QuizletSetsTask task = new QuizletSetsTask(server, userId);
        task.setCacheClient(storageClient);
        storageClient.setCacheMode(cacheMode);

        task.addTaskProgressListener(progressListener);

        return task;
    }
}
