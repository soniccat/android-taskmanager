package com.example.alexeyglushkov.service;

import android.os.Handler;
import android.os.HandlerThread;

import com.example.alexeyglushkov.authorization.Auth.Account;
import com.example.alexeyglushkov.authorization.Auth.AuthCredentials;
import com.example.alexeyglushkov.authorization.Auth.Authorizer;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommand;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommandProvider;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommandRunner;
import com.example.alexeyglushkov.authorization.service.Service;

import java.io.File;

/**
 * Created by alexeyglushkov on 26.11.15.
 */
public class SimpleService implements Service {
    private Account account;
    private ServiceCommandProvider commandProvider;
    private ServiceCommandRunner commandRunner;

    // to run authorization
    private HandlerThread authThread;
    private Handler authHandler;

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

    public void runCommand(ServiceCommand command) {
        runCommand(command, true);
    }

    @Override
    public void runCommand(final ServiceCommand command, final boolean needSign) {
        if (needSign && !account.isAuthorized()) {
            authorizeAndRun(command);
        } else {
            account.signCommand(command);
            commandRunner.run(command);
        }
    }

    private void authorizeAndRun(final ServiceCommand command) {
        runAsync(new Runnable() {
            @Override
            public void run() {
                account.authorize(new Authorizer.AuthorizerCompletion() {
                    @Override
                    public void onFinished(AuthCredentials credentials, Error error) {
                        if (error == null) {
                            runCommand(command, false);
                        } else {
                            commandRunner.cancel(command);
                        }
                    }
                });
            }
        });
    }

    private Handler getAuthHandler() {
        if (authHandler == null) {
            createAuthHandler();
        }

        return authHandler;
    }

    private void createAuthHandler() {
        authThread = new HandlerThread("SimpleService Auth Thread");
        authThread.start();
        authHandler = new Handler(authThread.getLooper());
    }

    private void runAsync(Runnable runnable) {
        Tools.runOnHandlerThread(getAuthHandler(), runnable);
    }
}