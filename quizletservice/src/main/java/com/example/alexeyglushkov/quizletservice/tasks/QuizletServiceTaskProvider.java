package com.example.alexeyglushkov.quizletservice.tasks;

import com.example.alexeyglushkov.authtaskmanager.ServiceTaskProvider;
import com.example.alexeyglushkov.cachemanager.Storage;
import com.example.alexeyglushkov.cachemanager.clients.Cache;
import com.example.alexeyglushkov.quizletservice.QuizletCommandProvider;
import com.example.alexeyglushkov.cachemanager.clients.SimpleCache;
import com.example.alexeyglushkov.streamlib.progress.ProgressListener;

/**
 * Created by alexeyglushkov on 03.04.16.
 */
public class QuizletServiceTaskProvider extends ServiceTaskProvider implements QuizletCommandProvider {
    @Override
    public com.example.alexeyglushkov.quizletservice.QuizletSetsCommand getLoadSetsCommand(String server, String userId, ProgressListener progressListener) {
        QuizletSetsCommand task = new QuizletSetsCommand(server, userId);

//        SimpleCache storageClient = new SimpleCache(storage, 0);
//        storageClient.setCacheMode(cacheMode);
//        task.setCacheClient(storageClient);

        task.getTask().addTaskProgressListener(progressListener);
        return task;
    }
}
