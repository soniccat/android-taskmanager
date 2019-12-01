package com.example.alexeyglushkov.quizletservice

import com.example.alexeyglushkov.authorization.Auth.ServiceCommandProvider
import com.example.alexeyglushkov.streamlib.progress.ProgressListener

/**
 * Created by alexeyglushkov on 03.04.16.
 */
interface QuizletCommandProvider : ServiceCommandProvider {
    fun getLoadSetsCommand(server: String, userId: String, progressListener: ProgressListener): QuizletSetsCommand
}