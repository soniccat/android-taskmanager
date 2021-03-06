package com.example.alexeyglushkov.quizletservice

import com.example.alexeyglushkov.authorization.Auth.ServiceCommand
import com.example.alexeyglushkov.quizletservice.entities.QuizletSet

/**
 * Created by alexeyglushkov on 03.04.16.
 */
interface QuizletSetsCommand : ServiceCommand<List<QuizletSet>>