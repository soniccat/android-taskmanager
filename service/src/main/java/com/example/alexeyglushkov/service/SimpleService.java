package com.example.alexeyglushkov.service;

import android.os.Handler;
import android.os.HandlerThread;

import com.example.alexeyglushkov.authorization.Auth.Account;
import com.example.alexeyglushkov.authorization.Auth.AuthCredentials;
import com.example.alexeyglushkov.authorization.Auth.Authorizer;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommand;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommandProvider;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommandProxy;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommandRunner;
import com.example.alexeyglushkov.authorization.service.Service;

/**
 * Created by alexeyglushkov on 26.11.15.
 */
public class SimpleService implements Service {
    private Account account;
    protected ServiceCommandProvider commandProvider;
    protected ServiceCommandRunner commandRunner;
    protected AuthCompletion authCompletion;

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

    @Override
    public void setAuthCompletion(AuthCompletion anAuthCompletion) {
        authCompletion = anAuthCompletion;
    }

    public void runCommand(ServiceCommandProxy proxy) {
        runCommand(proxy, true, authCompletion);
    }

    @Override
    public void runCommand(ServiceCommandProxy proxy, boolean canSignIn) {
        runCommand(proxy, canSignIn, authCompletion);
    }

    @Override
    public void runCommand(final ServiceCommandProxy proxy, final boolean canSignIn, AuthCompletion anAuthCompletion) {
        if (!account.isAuthorized()) {
            if (canSignIn) {
                authorizeAndRun(proxy, anAuthCompletion);

            } else if (anAuthCompletion != null) {
                anAuthCompletion.onFinished(proxy.getServiceCommand(), new AuthError(AuthError.Reason.NotAuthorized, null));
            }

        } else {
            ServiceCommand serviceCommand = proxy.getServiceCommand();
            account.signCommand(serviceCommand);
            commandRunner.run(serviceCommand);
        }
    }

    protected void authorizeAndRun(final ServiceCommandProxy proxy, final AuthCompletion anAuthCompletion) {
        authorize(new Authorizer.AuthorizerCompletion() {
            @Override
            public void onFinished(AuthCredentials credentials, Error error) {
                if (error == null) {
                    runCommand(proxy, false);

                } else if (authCompletion != null) {
                    anAuthCompletion.onFinished(proxy.getServiceCommand(), new AuthError(AuthError.Reason.InnerError, error));
                }
            }
        });
    }

    protected void authorize(final Authorizer.AuthorizerCompletion completion) {
        runAsync(new Runnable() {
            @Override
            public void run() {
                account.authorize(completion);
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

    public interface CommandCallback {
        void onCompleted(Error error);
    }
}
