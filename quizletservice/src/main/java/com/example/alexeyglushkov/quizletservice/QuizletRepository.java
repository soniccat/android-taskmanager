package com.example.alexeyglushkov.quizletservice;

import com.example.alexeyglushkov.cachemanager.Storage;
import com.example.alexeyglushkov.cachemanager.clients.StorageClient;
import com.example.alexeyglushkov.cachemanager.clients.SimpleStorageClient;
import com.example.alexeyglushkov.cachemanager.disk.DiskStorage;
import com.example.alexeyglushkov.quizletservice.entities.QuizletSet;
import com.example.alexeyglushkov.quizletservice.entities.QuizletTerm;
import com.example.alexeyglushkov.streamlib.codecs.ObjectCodec;
import com.example.alexeyglushkov.streamlib.progress.ProgressListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public class QuizletRepository {
    private @NonNull QuizletService service;
    private @NonNull StorageClient storageClient;

    @NonNull
    private NonNullMutableLiveData<Resource<List<QuizletSet>>> sets
            = new NonNullMutableLiveData<>(new Resource<List<QuizletSet>>());

    public QuizletRepository(@NonNull QuizletService service, @NonNull Storage storage) {
        this.service = service;
        if (storage instanceof DiskStorage) {
            DiskStorage diskStorage = (DiskStorage)storage;
            diskStorage.setSerializer(new ObjectCodec(), List.class);
        }
        storageClient = new SimpleStorageClient(storage, 0);
    }

    //// Actions

    public Single<List<QuizletSet>> loadSets(final ProgressListener progressListener) {
        final Resource.State previousState = sets.getValue().state;
        setState(Resource.State.Loading);

        return service.loadSets(progressListener)
                .doOnSuccess(new Consumer<List<QuizletSet>>() {
                    @Override
                    public void accept(List<QuizletSet> sets) throws Exception {
                        storageClient.putValue("quizlet_sets", sets);
                        setState(Resource.State.Loaded, sets);
                    }
                }).doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        setError(previousState, throwable);
                    }
                }).doOnDispose(new Action() {
                    @Override
                    public void run() throws Exception {
                        setState(previousState);
                    }
                });
    }

    public Single<List<QuizletSet>> restoreOrLoad(final ProgressListener progressListener) {
        setState(Resource.State.Loading);

        List<QuizletSet> sets = new ArrayList<>();
        try {
            List<QuizletSet> cachedValue = (List<QuizletSet>)storageClient.getCachedValue("quizlet_sets");
            if (cachedValue != null) {
                sets = cachedValue;
                setState(Resource.State.Restored, sets);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Single.just(sets);
        /*service.restoreSets(progressListener)
                .doOnSuccess(new Consumer<List<QuizletSet>>() {
                    @Override
                    public void accept(List<QuizletSet> sets) throws Exception {
                        setState(Resource.State.Restored, sets);
                    }
                }).onErrorResumeNext(new Function<Throwable, SingleSource<? extends List<QuizletSet>>>() {
                    @Override
                    public SingleSource<? extends List<QuizletSet>> apply(Throwable throwable) throws Exception {
                        setState(Resource.State.Uninitialized);
                        if (service.getAccount().isAuthorized()) {
                            return loadSets(progressListener);
                        }

                        return Single.error(throwable);
                    }
                }).doOnDispose(new Action() {
                    @Override
                    public void run() throws Exception {
                        setState(Resource.State.Uninitialized);
                    }
                });*/
    }

    //// Setters / Getters

    // Getters

    @NonNull
    public NonNullMutableLiveData<Resource<List<QuizletSet>>> getLiveSets() {
        return sets;
    }

    public List<QuizletSet> getSets() {
        return sets.getValue().data;
    }

    public List<QuizletTerm> getTerms() {
        List<QuizletTerm> terms = new ArrayList<>();
        for (QuizletSet set : getSets()) {
            terms.addAll(set.getTerms());
        }

        return terms;
    }

    // TODO: optimize
    public QuizletTerm getTerm(long termId) {
        QuizletTerm resultTerm = null;
        for (QuizletSet set : getSets()) {
            for (QuizletTerm term : set.getTerms()) {
                if (term.getId() == termId) {
                    resultTerm = term;
                    break;
                }
            }

            if (resultTerm != null) {
                break;
            }
        }

        return resultTerm;
    }

    public QuizletSet getSet(long id) {
        QuizletSet result = null;

        List<QuizletSet> setList = sets.getValue().data;
        if (setList != null) {
            for (QuizletSet set : setList) {
                if (set.getId() == id) {
                    result = set;
                    break;
                }
            }
        }

        return result;
    }

    // Setters

    private void setState(Resource.State newState) {
        sets.setValue(sets.getValue().resource(newState));
    }

    private void setState(Resource.State newState, List<QuizletSet> newSets) {
        sets.setValue(sets.getValue().resource(newState, newSets));
    }

    private void setError(Resource.State newState, Throwable newError) {
        sets.setValue(sets.getValue().resource(newState, newError));
    }
}
