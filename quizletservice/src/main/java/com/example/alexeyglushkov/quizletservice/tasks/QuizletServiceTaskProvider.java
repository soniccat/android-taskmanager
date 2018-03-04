package com.example.alexeyglushkov.quizletservice.tasks;

import com.example.alexeyglushkov.authtaskmanager.ServiceTaskProvider;
import com.example.alexeyglushkov.cachemanager.Storage;
import com.example.alexeyglushkov.cachemanager.clients.IStorageClient;
import com.example.alexeyglushkov.quizletservice.QuizletCommandProvider;
import com.example.alexeyglushkov.quizletservice.QuizletSetsCommand;
import com.example.alexeyglushkov.cachemanager.clients.StorageClient;
import com.example.alexeyglushkov.streamlib.progress.ProgressListener;

/**
 * Created by alexeyglushkov on 03.04.16.
 */
public class QuizletServiceTaskProvider extends ServiceTaskProvider implements QuizletCommandProvider {
    private Storage storage;

    public QuizletServiceTaskProvider(Storage storage) {
        this.storage = storage;
    }

    @Override
    public QuizletSetsCommand getLoadSetsCommand(String server, String userId, IStorageClient.CacheMode cacheMode, ProgressListener progressListener) {
        QuizletSetsTask task = new QuizletSetsTask(server, userId);

        StorageClient storageClient = new StorageClient(storage, 0);
        storageClient.setCacheMode(cacheMode);
        task.setCacheClient(storageClient);

        task.addTaskProgressListener(progressListener);
        return task;
    }
}
