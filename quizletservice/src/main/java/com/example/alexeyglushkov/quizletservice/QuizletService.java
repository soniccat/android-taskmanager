package com.example.alexeyglushkov.quizletservice;

import com.example.alexeyglushkov.authorization.Auth.Account;
import com.example.alexeyglushkov.authorization.Auth.AuthCredentials;
import com.example.alexeyglushkov.authorization.Auth.Authorizer;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommand;
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
        authorizeIfNeeded(new Authorizer.AuthorizerCompletion() {
            @Override
            public void onFinished(AuthCredentials credentials, Error error) {
                if (error != null) {
                    callback.onCompleted(error);
                } else {
                    loadSetsAuthorized(callback);
                }
            }
        });
    }

    private void loadSetsAuthorized(final CommandCallback callback) {
        final QuizletSetsCommand command = getQuizletCommandProvider().getLoadSetsCommand(server, getOAuthCredentials().getUserId());
        command.setServiceCommandCallback(new ServiceCommand.Callback() {
            @Override
            public void onCompleted() {
                if (command.getCommandError() == null) {
                    sets.clear();
                    sets.addAll(new ArrayList<>(Arrays.asList(command.getSets())));
                }

                callback.onCompleted(command.getCommandError());
            }
        });

        runCommand(command, true);
    }

    private OAuthCredentials getOAuthCredentials() {
        return (OAuthCredentials)getAccount().getCredentials();
    }

    private QuizletCommandProvider getQuizletCommandProvider() {
        return (QuizletCommandProvider)commandProvider;
    }


}
