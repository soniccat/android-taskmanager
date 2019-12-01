package com.example.alexeyglushkov.quizletservice.tasks

import com.example.alexeyglushkov.authtaskmanager.ServiceTaskProvider
import com.example.alexeyglushkov.quizletservice.QuizletCommandProvider
import com.example.alexeyglushkov.streamlib.progress.ProgressListener

/**
 * Created by alexeyglushkov on 03.04.16.
 */
class QuizletServiceTaskProvider : ServiceTaskProvider(), QuizletCommandProvider {
    override fun getLoadSetsCommand(server: String, userId: String, progressListener: ProgressListener?): QuizletSetsCommand {
        val task = QuizletSetsCommand(server, userId)
        //        SimpleCache storageClient = new SimpleCache(storage, 0);
//        storageClient.setCacheMode(cacheMode);
//        task.setCacheClient(storageClient);
        if (progressListener != null) {
            task.task.addTaskProgressListener(progressListener)
        }
        return task
    }
}