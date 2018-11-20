package com.example.alexeyglushkov.quizletservice;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.SingleSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.internal.operators.single.SingleJust;
import io.reactivex.plugins.RxJavaPlugins;

import com.example.alexeyglushkov.authorization.Auth.Account;
import com.example.alexeyglushkov.authorization.Auth.AuthCredentials;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommand;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommandProxy;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommandRunner;
import com.example.alexeyglushkov.authorization.OAuth.OAuthCredentials;
import com.example.alexeyglushkov.cachemanager.clients.IStorageClient;
import com.example.alexeyglushkov.quizletservice.entities.QuizletSet;
import com.example.alexeyglushkov.quizletservice.entities.QuizletTerm;
import com.example.alexeyglushkov.service.SimpleService;
import com.example.alexeyglushkov.streamlib.progress.ProgressListener;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by alexeyglushkov on 26.03.16.
 */
public class QuizletService extends SimpleService {

    static private final String SERVER = "https://api.quizlet.com/2.0";

    @NonNull
    private NonNullMutableLiveData<Resource<List<QuizletSet>>> sets
            = new NonNullMutableLiveData<>(new Resource<List<QuizletSet>>());

    //// Initialization

    public QuizletService(Account account, QuizletCommandProvider commandProvider, ServiceCommandRunner commandRunner) {
        setAccount(account);
        setServiceCommandProvider(commandProvider);
        setServiceCommandRunner(commandRunner);
    }

    //// Actions

    public Single<QuizletSetsCommand> loadSets(final ProgressListener progressListener) {
        final Resource.State previousState = sets.getValue().state;
//        ServiceCommand.CommandCallback callback = new ServiceCommand.CommandCallback() {
//            @Override
//            public void onCompleted(ServiceCommand command, Error error) {
//                if (error == null) {
//                    QuizletSetsCommand setsCommand = (QuizletSetsCommand)command;
//                    setState(Resource.State.Loaded, setsCommand.getSets());
//
//                } else {
//                    setError(previousState, error);
//                }
//            }
//        };

        setState(Resource.State.Loading);

        return authorizeIfNeeded()
        .flatMap(new Function<AuthCredentials, SingleSource<? extends QuizletSetsCommand>>() {
            @Override
            public SingleSource<? extends QuizletSetsCommand> apply(AuthCredentials authCredentials) throws Exception {
                QuizletSetsCommand command = createSetsCommand(IStorageClient.CacheMode.ONLY_STORE_TO_CACHE, progressListener);
                return runCommand(command, true);
            }
        }).doOnSuccess(new Consumer<QuizletSetsCommand>() {
            @Override
            public void accept(QuizletSetsCommand quizletSetsCommand) throws Exception {
                setState(Resource.State.Loaded, quizletSetsCommand.getSets());
            }
        }).doOnError(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                setError(previousState, throwable);
            }
        });
    }

    public Single<QuizletSetsCommand> restoreOrLoad(final ProgressListener progressListener) {
//        ServiceCommand.CommandCallback callback = new ServiceCommand.CommandCallback() {
//            @Override
//            public void onCompleted(ServiceCommand command, Error error) {
//                if (error == null) {
//                    QuizletSetsCommand setsCommand = (QuizletSetsCommand)command;
//                    setState(Resource.State.Restored, setsCommand.getSets());
//                } else {
//                    setState(Resource.State.Uninitialized);
//                    if (getAccount().isAuthorized()) {
//                        loadSets(progressListener);
//                    }
//                }
//            }
//        };

        setState(Resource.State.Loading);

        return justOrError(getAccount().getCredentials())
            .flatMap(new Function<AuthCredentials, SingleSource<? extends QuizletSetsCommand>>() {
                @Override
                public SingleSource<? extends QuizletSetsCommand> apply(AuthCredentials authCredentials) throws Exception {
                    QuizletSetsCommand command = createSetsCommand(IStorageClient.CacheMode.ONLY_LOAD_FROM_CACHE, progressListener);
                    return runCommand(command, false);
                }
            }).doOnSuccess(new Consumer<QuizletSetsCommand>() {
                @Override
                public void accept(QuizletSetsCommand quizletSetsCommand) throws Exception {
                    setState(Resource.State.Restored, quizletSetsCommand.getSets());
                }
            }).onErrorResumeNext(new Function<Throwable, SingleSource<? extends QuizletSetsCommand>>() {
                @Override
                public SingleSource<? extends QuizletSetsCommand> apply(Throwable throwable) throws Exception {
                    setState(Resource.State.Uninitialized);
                    if (getAccount().isAuthorized()) {
                        return loadSets(progressListener);
                    }

                    return Single.error(throwable);
                }
            });
    }

    public static <T> Single<T> justOrError(@Nullable final T item) {
        return item != null ? Single.just(item) : Single.<T>error(new Error("not authorized"));
    }

    private Single<AuthCredentials> authorizeIfNeeded() {
        if (!getAccount().isAuthorized()) {
            return authorize();
        } else {
            return justOrError(getAccount().getCredentials());
        }
    }

//    @NonNull
//    private ServiceCommandProxy createSetsCommandProxy(final IStorageClient.CacheMode cacheMode, final ProgressListener progressListener) {
//        return new ServiceCommandProxy() {
//            private ServiceCommand cmd = null;
//
//            @Override
//            public ServiceCommand getServiceCommand() {
//                if (cmd == null) {
//                    cmd = createSetsCommand(cacheMode, progressListener);
//                }
//
//                return cmd;
//            }
//
//            @Override
//            public boolean isEmpty() {
//                return cmd == null;
//            }
//        };
//    }

    @NonNull
    private QuizletSetsCommand createSetsCommand(final IStorageClient.CacheMode cacheMode, final ProgressListener progressListener) {
        final QuizletSetsCommand command = getQuizletCommandProvider().getLoadSetsCommand(SERVER, getOAuthCredentials().getUserId(), cacheMode, progressListener);
        return command;
    }

    //// Setters

    private void setState(Resource.State newState) {
        sets.setValue(sets.getValue().resource(newState));
    }

    private void setState(Resource.State newState, List<QuizletSet> newSets) {
        sets.setValue(sets.getValue().resource(newState, newSets));
    }

    private void setError(Resource.State newState, Throwable newError) {
        sets.setValue(sets.getValue().resource(newState, newError));
    }

    //// Getters

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

    // Cast Getters

    private OAuthCredentials getOAuthCredentials() {
        return (OAuthCredentials)getAccount().getCredentials();
    }

    private QuizletCommandProvider getQuizletCommandProvider() {
        return (QuizletCommandProvider)commandProvider;
    }
}
