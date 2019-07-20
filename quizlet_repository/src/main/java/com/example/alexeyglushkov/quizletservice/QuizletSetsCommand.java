package com.example.alexeyglushkov.quizletservice;

import com.example.alexeyglushkov.authorization.Auth.ServiceCommand;
import com.example.alexeyglushkov.quizletservice.entities.QuizletSet;

import java.util.List;

/**
 * Created by alexeyglushkov on 03.04.16.
 */
public interface QuizletSetsCommand extends ServiceCommand<List<QuizletSet>> {
}