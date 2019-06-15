package com.example.alexeyglushkov.wordteacher.quizletlistmodules.setlistmodule.presenter;

import android.os.Bundle;

import com.example.alexeyglushkov.quizletservice.QuizletRepository;
import com.example.alexeyglushkov.quizletservice.entities.QuizletSet;
import com.example.alexeyglushkov.wordteacher.listmodule.ResourceListLiveDataProviderImp;

public class QuizletSetListLiveDataProvider extends ResourceListLiveDataProviderImp<QuizletSet> {
    public QuizletSetListLiveDataProvider(Bundle bundle, QuizletRepository repository) {
        super(bundle, repository);
    }
}
