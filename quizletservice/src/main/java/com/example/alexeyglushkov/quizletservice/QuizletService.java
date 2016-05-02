package com.example.alexeyglushkov.quizletservice;

import android.support.annotation.NonNull;

import com.example.alexeyglushkov.authorization.Auth.Account;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommand;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommandProxy;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommandRunner;
import com.example.alexeyglushkov.authorization.OAuth.OAuthCredentials;
import com.example.alexeyglushkov.quizletservice.entities.QuizletSet;
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

    public void loadSets(final CommandCallback callback) {
        runCommand(createSetsCommandProxy(callback), true, createAuthCompletion(callback));
    }

    public List<QuizletSet> getSets() {
        return sets;
    }

    @NonNull
    private ServiceCommandProxy createSetsCommandProxy(final CommandCallback callback) {
        return new ServiceCommandProxy() {
            @Override
            public ServiceCommand getServiceCommand() {
                return createSetsCommand(callback);
            }
        };
    }

    @NonNull
    private QuizletSetsCommand createSetsCommand(final CommandCallback callback) {
        final QuizletSetsCommand command = getQuizletCommandProvider().getLoadSetsCommand(server, getOAuthCredentials().getUserId());
        command.setServiceCommandCallback(new ServiceCommand.Callback() {
            @Override
            public void onCompleted() {
                if (command.getCommandError() == null) {
                    sets.clear();
                    sets.addAll(new ArrayList<>(Arrays.asList(command.getSets())));
                }

                if (callback != null) {
                    callback.onCompleted(command.getCommandError());
                }
            }
        });
        return command;
    }

    @NonNull
    private AuthCompletion createAuthCompletion(final CommandCallback callback) {
        return new AuthCompletion() {
            @Override
            public void onFinished(ServiceCommand command, AuthError error) {
                if (callback != null) {
                    callback.onCompleted(error);
                }
            }
        };
    }

    private OAuthCredentials getOAuthCredentials() {
        return (OAuthCredentials)getAccount().getCredentials();
    }

    private QuizletCommandProvider getQuizletCommandProvider() {
        return (QuizletCommandProvider)commandProvider;
    }


}
