package com.example.alexeyglushkov.wordteacher.quizletlistmodules.setlistmodule.presenter;

import android.os.Bundle;

import com.example.alexeyglushkov.quizletservice.NonNullLiveData;
import com.example.alexeyglushkov.quizletservice.QuizletRepository;
import com.example.alexeyglushkov.quizletservice.Resource;
import com.example.alexeyglushkov.quizletservice.entities.QuizletSet;
import com.example.alexeyglushkov.wordteacher.listmodule.ListLiveDataProvider;
import com.example.alexeyglushkov.wordteacher.listmodule.ListLiveDataProviderFromResource;

import java.util.List;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

public class QuizletSetListLiveDataProvider extends ListLiveDataProviderFromResource<QuizletSet> {
    private QuizletRepository repository;

    public QuizletSetListLiveDataProvider(QuizletRepository repository) {
        super(repository);
        this.repository = repository;
    }

    public QuizletSetListLiveDataProvider(Bundle bundle, QuizletRepository repository) {
        super(bundle, repository);
    }

    @Override
    public LiveData<List<QuizletSet>> getListLiveData() {
        return Transformations.map(repository.getLiveData(), new Function<Resource<List<QuizletSet>>, List<QuizletSet>>() {
            @Override
            public List<QuizletSet> apply(Resource<List<QuizletSet>> input) {
                return input.data;
            }
        });
    }
}
