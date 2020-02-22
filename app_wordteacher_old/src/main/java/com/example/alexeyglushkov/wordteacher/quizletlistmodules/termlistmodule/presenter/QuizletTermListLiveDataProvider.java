package com.example.alexeyglushkov.wordteacher.quizletlistmodules.termlistmodule.presenter;

import android.os.Bundle;

import androidx.lifecycle.LiveData;

import com.example.alexeyglushkov.quizletservice.QuizletRepository;
import com.aglushkov.repository.livedata.Resource;
import com.aglushkov.repository.livedata.ResourceLiveDataProvider;
import com.example.alexeyglushkov.quizletservice.entities.QuizletTerm;
import com.example.alexeyglushkov.wordteacher.listmodule.ResourceListLiveDataProviderImp;

import java.util.List;


public class QuizletTermListLiveDataProvider extends ResourceListLiveDataProviderImp<QuizletTerm> {
    private long setId = -1;

    public QuizletTermListLiveDataProvider(Bundle bundle, final QuizletRepository repository, final long setId) {
        super(bundle, null);
        this.setId = setId;
        setResourceLiveDataProvider(new ResourceLiveDataProvider<List<QuizletTerm>>() {
            @Override
            public LiveData<Resource<List<QuizletTerm>>> getLiveData() {
                return repository.getTermListLiveData(setId);
            }
        });
    }

    @Override
    public void store(Bundle bundle) {
        super.store(bundle);
        bundle.putLong("setId", this.setId);
    }

    @Override
    public void restore(Bundle bundle) {
        super.restore(bundle);
        setId = bundle.getLong("setId");
    }
}
