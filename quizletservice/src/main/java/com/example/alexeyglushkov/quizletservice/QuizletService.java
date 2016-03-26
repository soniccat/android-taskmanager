package com.example.alexeyglushkov.quizletservice;

import com.example.alexeyglushkov.authorization.Auth.Account;
import com.example.alexeyglushkov.authorization.Auth.AuthCredentials;
import com.example.alexeyglushkov.authorization.Auth.Authorizer;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommand;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommandProvider;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommandRunner;
import com.example.alexeyglushkov.authorization.OAuth.OAuthCredentials;
import com.example.alexeyglushkov.authorization.requestbuilder.HttpUrlConnectionBuilder;
import com.example.alexeyglushkov.service.SimpleService;

import junit.framework.Assert;

/**
 * Created by alexeyglushkov on 26.03.16.
 */
public class QuizletService extends SimpleService {
    static final String server = "https://api.quizlet.com/2.0";

    public QuizletService(Account account, ServiceCommandProvider commandProvider, ServiceCommandRunner commandRunner) {
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
        HttpUrlConnectionBuilder requestBuilder = new HttpUrlConnectionBuilder();

        String url = server + "/users/" + getOAuthCredentials().getUserId() + "/sets";
        requestBuilder.setUrl(url);

        final ServiceCommand command = commandProvider.getServiceCommand(requestBuilder);
        command.setServiceCommandCallback(new ServiceCommand.Callback() {
            @Override
            public void onCompleted() {
                callback.onCompleted(command.getCommandError());
            }
        });
        runCommand(command, true);
    }

    private OAuthCredentials getOAuthCredentials() {
        return (OAuthCredentials)getAccount().getCredentials();
    }
}
