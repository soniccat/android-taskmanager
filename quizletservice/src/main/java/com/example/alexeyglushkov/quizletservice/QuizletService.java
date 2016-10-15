package com.example.alexeyglushkov.quizletservice;

import android.support.annotation.NonNull;

import com.example.alexeyglushkov.authorization.Auth.Account;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommand;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommandProxy;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommandRunner;
import com.example.alexeyglushkov.authorization.OAuth.OAuthCredentials;
import com.example.alexeyglushkov.quizletservice.entities.QuizletSet;
import com.example.alexeyglushkov.quizletservice.entities.QuizletTerm;
import com.example.alexeyglushkov.service.CachableHttpLoadTask;
import com.example.alexeyglushkov.service.SimpleService;
import com.example.alexeyglushkov.taskmanager.task.WeakRefList;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by alexeyglushkov on 26.03.16.
 */
public class QuizletService extends SimpleService {

    // on error we return back to the state before loading
    public enum State {
        Unitialized,
        Restored,
        Loaded,
        Loading
    }

    static private final String SERVER = "https://api.quizlet.com/2.0";

    private List<QuizletSet> sets = new ArrayList<>();
    private WeakRefList<QuizletServiceListener> listeners = new WeakRefList<>();

    private State state = State.Unitialized;

    //// Initialization

    public QuizletService(Account account, QuizletCommandProvider commandProvider, ServiceCommandRunner commandRunner) {
        setAccount(account);
        setServiceCommandProvider(commandProvider);
        setServiceCommandRunner(commandRunner);
    }

    //// Events

    private void onStateChanged(State oldState) {
        for (WeakReference<QuizletServiceListener> ref : listeners) {
            ref.get().onStateChanged(this, oldState);
        }
    }

    private void onSetsLoadError(Error error) {
        for (WeakReference<QuizletServiceListener> ref : listeners) {
            ref.get().onLoadError(this, error);
        }
    }

    //// Actions

    public void loadSets(CachableHttpLoadTask.CacheMode cacheMode) {
        ServiceCommand.CommandCallback callback = createLoadCallback(State.Loaded);
        setState(State.Loading);
        runCommand(createSetsCommandProxy(callback, cacheMode), true, callback);
    }

    public void restore() {
        ServiceCommand.CommandCallback callback = createLoadCallback(State.Restored);
        setState(State.Loading);
        runCommand(createSetsCommandProxy(callback, CachableHttpLoadTask.CacheMode.ONLY_LOAD_FROM_CACHE), false, callback);
    }

    @NonNull
    private ServiceCommandProxy createSetsCommandProxy(final ServiceCommand.CommandCallback callback, final CachableHttpLoadTask.CacheMode cacheMode) {
        return new ServiceCommandProxy() {
            @Override
            public ServiceCommand getServiceCommand() {
                return createSetsCommand(callback, cacheMode);
            }
        };
    }

    @NonNull
    private QuizletSetsCommand createSetsCommand(final ServiceCommand.CommandCallback callback, final CachableHttpLoadTask.CacheMode cacheMode) {
        final QuizletSetsCommand command = getQuizletCommandProvider().getLoadSetsCommand(SERVER, getOAuthCredentials().getUserId(), cacheMode);
        command.setServiceCommandCallback(new ServiceCommand.CommandCallback() {
            @Override
            public void onCompleted(Error error) {

                // TODO: try to put this logic in SimpleService with option
                if (command.getResponseCode() == 401) {
                    authorizeAndRun(createSetsCommand(callback, cacheMode), callback);

                } else {
                    if (error == null) {
                        sets.clear();
                        sets.addAll(new ArrayList<>(Arrays.asList(command.getSets())));
                    }

                    if (callback != null) {
                        callback.onCompleted(error);
                    }
                }
            }
        });
        return command;
    }

    // Listeners

    public void addListener(QuizletServiceListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(new WeakReference<>(listener));
        }
    }

    public void removeListener(QuizletServiceListener listener) {
        listeners.remove(listener);
    }

    //// Creation Methods

    @NonNull
    private ServiceCommand.CommandCallback createLoadCallback(final State successState) {
        final State oldState = this.state;
        return new ServiceCommand.CommandCallback() {
            @Override
            public void onCompleted(Error error) {
                if (error == null) {
                    setState(successState);

                } else {
                    setState(oldState);
                    onSetsLoadError(error);
                }
            }
        };
    }

    //// Setters

    private void setState(State state) {
        State oldState = this.state;
        this.state = state;
        onStateChanged(oldState);
    }

    //// Getters

    public List<QuizletSet> getSets() {
        return sets;
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

        for (QuizletSet set : sets) {
            if (set.getId() == id) {
                result = set;
                break;
            }
        }

        return result;
    }

    public State getState() {
        return state;
    }

    // Cast Getters

    private OAuthCredentials getOAuthCredentials() {
        return (OAuthCredentials)getAccount().getCredentials();
    }

    private QuizletCommandProvider getQuizletCommandProvider() {
        return (QuizletCommandProvider)commandProvider;
    }

    //// Interfaces

    public interface QuizletServiceListener {
        void onStateChanged(QuizletService service, State oldState);
        void onLoadError(QuizletService service, Error error);
    }
}
