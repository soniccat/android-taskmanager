package com.example.alexeyglushkov.wordteacher.quizletlistmodules.setlistmodule.presenter;

import android.os.Bundle;

import com.example.alexeyglushkov.quizletservice.QuizletRepository;
import com.example.alexeyglushkov.quizletservice.Resource;
import com.example.alexeyglushkov.quizletservice.entities.QuizletSet;
import com.example.alexeyglushkov.wordteacher.listmodule.ResourceListLiveDataProviderImp;

import java.util.List;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

public class QuizletSetListLiveDataProvider extends ResourceListLiveDataProviderImp<QuizletSet> {
    public QuizletSetListLiveDataProvider(Bundle bundle, QuizletRepository repository) {
        super(bundle, repository);
    }
}
