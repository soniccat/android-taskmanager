package com.example.alexeyglushkov.quizletservice;

import androidx.annotation.NonNull;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.Function;
import tools.RxTools;

import com.example.alexeyglushkov.authorization.Auth.Account;
import com.example.alexeyglushkov.authorization.Auth.AuthCredentials;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommandRunner;
import com.example.alexeyglushkov.authorization.OAuth.OAuthCredentials;
import com.example.alexeyglushkov.cachemanager.clients.StorageClient;
import com.example.alexeyglushkov.quizletservice.entities.QuizletSet;
import com.example.alexeyglushkov.service.SimpleService;
import com.example.alexeyglushkov.streamlib.progress.ProgressListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by alexeyglushkov on 26.03.16.
 */
public class QuizletService extends SimpleService {

    static private final String SERVER = "https://api.quizlet.com/2.0";

    //// Initialization

    public QuizletService(Account account, QuizletCommandProvider commandProvider, ServiceCommandRunner commandRunner) {
        setAccount(account);
        setServiceCommandProvider(commandProvider);
        setServiceCommandRunner(commandRunner);
    }

    //// Actions

    public Single<List<QuizletSet>> loadSets(final ProgressListener progressListener) {
        return authorizeIfNeeded()
        .flatMap(new Function<AuthCredentials, SingleSource<? extends QuizletSetsCommand>>() {
            @Override
            public SingleSource<? extends QuizletSetsCommand> apply(AuthCredentials authCredentials) throws Exception {
                QuizletSetsCommand command = createSetsCommand(StorageClient.CacheMode.IGNORE_CACHE, progressListener);
                return runCommand(command, true);
            }
        }).flatMap(new Function<QuizletSetsCommand, SingleSource<? extends List<QuizletSet>>>() {
            @Override
            public SingleSource<? extends List<QuizletSet>> apply(QuizletSetsCommand quizletSetsCommand) throws Exception {
                return Single.just(new ArrayList<>(Arrays.asList(quizletSetsCommand.getResponse())));
            }
        });
    }

    public Single<List<QuizletSet>> restoreSets(final ProgressListener progressListener) {
        return RxTools.justOrError(getAccount().getCredentials())
            .flatMap(new Function<AuthCredentials, SingleSource<? extends QuizletSetsCommand>>() {
                @Override
                public SingleSource<? extends QuizletSetsCommand> apply(AuthCredentials authCredentials) throws Exception {
                    QuizletSetsCommand command = createSetsCommand(StorageClient.CacheMode.IGNORE_CACHE, progressListener);
                    return runCommand(command, false);
                }
            }).flatMap(new Function<QuizletSetsCommand, SingleSource<? extends List<QuizletSet>>>() {
                @Override
                public SingleSource<? extends List<QuizletSet>> apply(QuizletSetsCommand quizletSetsCommand) throws Exception {
                    return Single.just(new ArrayList<>(Arrays.asList(quizletSetsCommand.getResponse())));
                }
            });
    }

    @NonNull
    private QuizletSetsCommand createSetsCommand(final StorageClient.CacheMode cacheMode, final ProgressListener progressListener) {
        final QuizletSetsCommand command = getQuizletCommandProvider().getLoadSetsCommand(SERVER, getOAuthCredentials().getUserId(), cacheMode, progressListener);
        return command;
    }

    // Cast Getters

    private OAuthCredentials getOAuthCredentials() {
        return (OAuthCredentials)getAccount().getCredentials();
    }

    private QuizletCommandProvider getQuizletCommandProvider() {
        return (QuizletCommandProvider)commandProvider;
    }
}
