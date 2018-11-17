package com.example.alexeyglushkov.quizletservice;

import androidx.annotation.NonNull;

import com.example.alexeyglushkov.authorization.Auth.Account;
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

    public ServiceCommandProxy loadSets(ProgressListener progressListener) {
        final Resource.State previousState = sets.getValue().state;
        ServiceCommand.CommandCallback callback = new ServiceCommand.CommandCallback() {
            @Override
            public void onCompleted(ServiceCommand command, Error error) {
                if (error == null) {
                    QuizletSetsCommand setsCommand = (QuizletSetsCommand)command;
                    setState(Resource.State.Loaded, setsCommand.getSets());

                } else {
                    setError(previousState, error);
                }
            }
        };

        setState(Resource.State.Loading);

        ServiceCommandProxy proxy = createSetsCommandProxy(callback, IStorageClient.CacheMode.ONLY_STORE_TO_CACHE, progressListener);
        runCommand(proxy, true, callback);

        return proxy;
    }

    public ServiceCommandProxy restoreOrLoad(final ProgressListener progressListener) {
        ServiceCommand.CommandCallback callback = new ServiceCommand.CommandCallback() {
            @Override
            public void onCompleted(ServiceCommand command, Error error) {
                if (error == null) {
                    QuizletSetsCommand setsCommand = (QuizletSetsCommand)command;
                    setState(Resource.State.Restored, setsCommand.getSets());
                } else {
                    setState(Resource.State.Uninitialized);
                    if (getAccount().isAuthorized()) {
                        loadSets(progressListener);
                    }
                }
            }
        };
        setState(Resource.State.Loading);

        ServiceCommandProxy proxy = createSetsCommandProxy(callback, IStorageClient.CacheMode.ONLY_LOAD_FROM_CACHE, progressListener);
        runCommand(proxy, false, callback);

        return proxy;
    }

    @NonNull
    private ServiceCommandProxy createSetsCommandProxy(@NonNull final ServiceCommand.CommandCallback callback, final IStorageClient.CacheMode cacheMode, final ProgressListener progressListener) {
        return new ServiceCommandProxy() {
            private ServiceCommand cmd = null;

            @Override
            public ServiceCommand getServiceCommand() {
                if (cmd == null) {
                    cmd = createSetsCommand(callback, cacheMode, progressListener);
                }

                return cmd;
            }

            @Override
            public boolean isEmpty() {
                return cmd == null;
            }
        };
    }

    @NonNull
    private QuizletSetsCommand createSetsCommand(@NonNull final ServiceCommand.CommandCallback callback, final IStorageClient.CacheMode cacheMode, final ProgressListener progressListener) {
        final QuizletSetsCommand command = getQuizletCommandProvider().getLoadSetsCommand(SERVER, getOAuthCredentials().getUserId(), cacheMode, progressListener);
        command.setServiceCommandCallback(new ServiceCommand.CommandCallback() {
            @Override
            public void onCompleted(ServiceCommand command, Error error) {

                // TODO: try to put this logic in SimpleService with option
                if (command.getResponseCode() == 401) {
                    command.clear();

                    authorizeAndRun(command, callback);
                } else {
                    callback.onCompleted(command, error);
                }
            }
        });
        return command;
    }

    //// Setters

    private void setState(Resource.State newState) {
        sets.setValue(sets.getValue().resource(newState));
    }

    private void setState(Resource.State newState, List<QuizletSet> newSets) {
        sets.setValue(sets.getValue().resource(newState, newSets));
    }

    private void setError(Resource.State newState, Error newError) {
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
