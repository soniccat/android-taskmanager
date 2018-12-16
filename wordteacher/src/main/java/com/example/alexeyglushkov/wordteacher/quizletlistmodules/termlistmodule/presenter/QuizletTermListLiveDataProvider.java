package com.example.alexeyglushkov.wordteacher.quizletlistmodules.termlistmodule.presenter;

import android.os.Bundle;

import com.example.alexeyglushkov.quizletservice.QuizletRepository;
import com.example.alexeyglushkov.quizletservice.Resource;
import com.example.alexeyglushkov.quizletservice.ResourceLiveDataProvider;
import com.example.alexeyglushkov.quizletservice.entities.QuizletSet;
import com.example.alexeyglushkov.quizletservice.entities.QuizletTerm;
import com.example.alexeyglushkov.wordteacher.listmodule.ResourceListLiveDataProvider;
import com.example.alexeyglushkov.wordteacher.listmodule.ResourceListLiveDataProviderImp;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

public class QuizletTermListLiveDataProvider extends ResourceListLiveDataProviderImp<QuizletTerm> {
    private QuizletRepository repository;
    private @NonNull Resource<List<QuizletTerm>> aResource = new Resource<>();

    public QuizletTermListLiveDataProvider(QuizletRepository repository) {
        super(null);
        this.repository = repository;
        setResourceLiveDataProvider(new Adapter());
    }

    public QuizletTermListLiveDataProvider(Bundle bundle, QuizletRepository repository) {
        super(bundle, null);
        this.repository = repository;
        setResourceLiveDataProvider(new Adapter());
    }

    // QuizletSet liveData to QuizletTerm liveData
    private class Adapter implements ResourceLiveDataProvider<List<QuizletTerm>> {
        public Adapter() {
        }

        @Override
        public LiveData<Resource<List<QuizletTerm>>> getLiveData() {
            final MediatorLiveData<Resource<List<QuizletTerm>>> mediatorLiveData = new MediatorLiveData<>();
            mediatorLiveData.setValue(aResource);

            mediatorLiveData.addSource(repository.getLiveData(), new Observer<Resource<List<QuizletSet>>>() {
                @Override
                public void onChanged(Resource<List<QuizletSet>> listResource) {
                    mediatorLiveData.setValue(buildFinalResource(listResource));
                }
            });

            return mediatorLiveData;
        }

        private Resource<List<QuizletTerm>> buildFinalResource(Resource<List<QuizletSet>> listResource) {
            aResource.update(repository.getTerms());
            return aResource;
        }
    }
}
