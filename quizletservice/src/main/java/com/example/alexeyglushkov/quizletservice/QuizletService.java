package com.example.alexeyglushkov.quizletservice;

import android.util.Log;

import com.example.alexeyglushkov.authorization.Auth.Account;
import com.example.alexeyglushkov.authorization.Auth.AuthCredentials;
import com.example.alexeyglushkov.authorization.Auth.Authorizer;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommand;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommandProvider;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommandRunner;
import com.example.alexeyglushkov.authorization.OAuth.OAuthCredentials;
import com.example.alexeyglushkov.authorization.OAuth.Token;
import com.example.alexeyglushkov.authorization.requestbuilder.HttpUrlConnectionBuilder;
import com.example.alexeyglushkov.service.SimpleService;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import junit.framework.Assert;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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
        final ServiceCommand command = getQuizletCommandProvider().getLoadSetsCommand(server, getOAuthCredentials().getUserId());
        command.setServiceCommandCallback(new ServiceCommand.Callback() {
            @Override
            public void onCompleted() {
                if (command.getCommandError() == null) {
                    parseSets(command.getResponse());
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

    private void parseSets(String setsResponse) {
        try {
            SimpleModule md = new SimpleModule("QuizletModule", new Version(1,0,0,null,null,null));
            md.addDeserializer(QuizletSet.class, new QuizletSetDeserializer(QuizletSet.class));
            md.addDeserializer(QuizletUser.class, new QuizletUserDeserializer(QuizletUser.class));
            md.addDeserializer(QuizletTerm.class, new QuizletTermDeserializer(QuizletTerm.class));

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(md);

            QuizletSet[] setArray = mapper.readValue(setsResponse, QuizletSet[].class);
            sets.addAll(Arrays.asList(setArray));
            Log.d("QuizletService", "success");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
