package com.example.alexeyglushkov.service;

import android.os.HandlerThread;

import com.example.alexeyglushkov.authorization.Auth.Account;
import com.example.alexeyglushkov.authorization.Auth.AuthCredentials;
import com.example.alexeyglushkov.authorization.Auth.Authorizer;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommand;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommandProvider;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommandRunner;
import com.example.alexeyglushkov.authorization.service.Service;

import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import tools.RxTools;

/**
 * Created by alexeyglushkov on 26.11.15.
 */
public class SimpleService implements Service {
    private Account account;

    protected ServiceCommandProvider commandProvider;
    protected ServiceCommandRunner commandRunner;

    // to run authorization
    private HandlerThread authThread;

    public void init() {
    }

    @Override
    public Account getAccount() {
        return this.account;
    }

    @Override
    public void setAccount(Account account) {
        this.account = account;
    }

    @Override
    public void setServiceCommandProvider(ServiceCommandProvider provider) {
        this.commandProvider = provider;
    }

    @Override
    public void setServiceCommandRunner(ServiceCommandRunner runner) {
        this.commandRunner = runner;
    }

    @Override
    public <T extends ServiceCommand> Single<T> runCommand(final T command, final boolean canSignIn) {
        if (!account.isAuthorized()) {
            if (canSignIn) {
                return authorizeAndRun(command);

            } else {
                Error error = new Authorizer.AuthError(Authorizer.AuthError.Reason.NotAuthorized, null);
                return Single.error(error);
            }

        } else {
            account.signCommand(command);
            return runCommand(command);
        }
    }

    public <T extends ServiceCommand> Single<T> runCommand(final T command) {
        return commandRunner.run(command)
                .onErrorResumeNext(new Function<Throwable, SingleSource<T>>() {
                    @Override
                    public SingleSource<T> apply(Throwable throwable) throws Exception {
                        if (command.getResponseCode() == 401) {
                            command.clear();
                            return authorizeAndRun(command);
                        } else {
                            return Single.error(throwable);
                        }
                    }
                });
    }

    public <T extends ServiceCommand> Single<T> authorizeAndRun(final T command) {
        return authorize().flatMap(new Function<AuthCredentials, SingleSource<T>>() {
            @Override
            public SingleSource<T> apply(AuthCredentials authCredentials) throws Exception {
                return runCommand(command, false);
            }
        });
    }

    public Single<AuthCredentials> authorizeIfNeeded() {
        if (!getAccount().isAuthorized()) {
            return authorize();
        } else {
            return RxTools.justOrError(getAccount().getCredentials());
        }
    }

    public Single<AuthCredentials> authorize() {
        startAuthThreadIfNeeded();
        return account.authorize().subscribeOn(AndroidSchedulers.from(authThread.getLooper()));
    }

    private void startAuthThreadIfNeeded() {
        if (authThread == null) {
            authThread = new HandlerThread("SimpleService Auth Thread");
            authThread.start();
        }
    }

    public void cancel(ServiceCommand cmd) {
        commandRunner.cancel(cmd);
    }
}
