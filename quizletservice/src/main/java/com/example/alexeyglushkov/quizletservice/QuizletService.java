package com.example.alexeyglushkov.quizletservice;

import android.support.annotation.NonNull;

import com.example.alexeyglushkov.authorization.Auth.Account;
import com.example.alexeyglushkov.authorization.Auth.Authorizer;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommand;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommandProxy;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommandRunner;
import com.example.alexeyglushkov.authorization.OAuth.OAuthCredentials;
import com.example.alexeyglushkov.quizletservice.entities.QuizletSet;
import com.example.alexeyglushkov.service.CachableHttpLoadTask;
import com.example.alexeyglushkov.service.SimpleService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by alexeyglushkov on 26.03.16.
 */
public class QuizletService extends SimpleService {
    static final String server = "https://api.quizlet.com/2.0";

    private List<QuizletSet> sets = new ArrayList<>();

    public QuizletService(Account account, QuizletCommandProvider commandProvider, ServiceCommandRunner commandRunner) {
        setAccount(account);
        setServiceCommandProvider(commandProvider);
        setServiceCommandRunner(commandRunner);
    }

    public void loadSets(final ServiceCommand.CommandCallback callback, CachableHttpLoadTask.CacheMode cacheMode) {
        runCommand(createSetsCommandProxy(callback, cacheMode), true, callback);
    }

    public void restore(final ServiceCommand.CommandCallback callback) {
        runCommand(createSetsCommandProxy(callback, CachableHttpLoadTask.CacheMode.ONLY_LOAD_FROM_CACHE), false, callback);
    }

    public List<QuizletSet> getSets() {
        return sets;
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
        final QuizletSetsCommand command = getQuizletCommandProvider().getLoadSetsCommand(server, getOAuthCredentials().getUserId(), cacheMode);
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

    private OAuthCredentials getOAuthCredentials() {
        return (OAuthCredentials)getAccount().getCredentials();
    }

    private QuizletCommandProvider getQuizletCommandProvider() {
        return (QuizletCommandProvider)commandProvider;
    }


}
