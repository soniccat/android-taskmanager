package com.example.alexeyglushkov.wordteacher.quizletlistmodules.setlistmodule.presenter;

import com.example.alexeyglushkov.quizletservice.NonNullLiveData;
import com.example.alexeyglushkov.quizletservice.QuizletRepository;
import com.example.alexeyglushkov.quizletservice.Resource;
import com.example.alexeyglushkov.quizletservice.entities.QuizletSet;
import com.example.alexeyglushkov.wordteacher.listmodule.ListLiveDataProvider;

import java.util.List;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

public class QuizletSetListLiveDataProvider implements ListLiveDataProvider<List<QuizletSet>> {
    private QuizletRepository repository;

    public QuizletSetListLiveDataProvider(QuizletRepository repository) {
        this.repository = repository;
    }

    @Override
    public LiveData<List<QuizletSet>> getListLiveData() {
        return Transformations.map(repository.getLiveSets(), new Function<Resource<List<QuizletSet>>, List<QuizletSet>>() {
            @Override
            public List<QuizletSet> apply(Resource<List<QuizletSet>> input) {
                return input.data;
            }
        });
    }
}
