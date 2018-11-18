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
import com.example.alexeyglushkov.tools.HandlerTools;

import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * Created by alexeyglushkov on 26.11.15.
 */
public class SimpleService implements Service {
    private Account account;

    // TODO: could be not necessary
    protected ServiceCommandProvider commandProvider;
    protected ServiceCommandRunner commandRunner;
    //protected ServiceCommand.CommandCallback authCompletion;

    // to run authorization
    private HandlerThread authThread;
    //private Handler authHandler;

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

//    @Override
//    public void setAuthCompletion(ServiceCommand.CommandCallback anAuthCompletion) {
//        authCompletion = anAuthCompletion;
//    }

    public Single<ServiceCommand> runCommand(ServiceCommandProxy proxy) {
        return runCommand(proxy, true/*, authCompletion*/);
    }

//    @Override
//    public void runCommand(ServiceCommandProxy proxy, boolean canSignIn) {
//        runCommand(proxy, canSignIn, authCompletion);
//    }

    @Override
    public Single<ServiceCommand> runCommand(final ServiceCommandProxy proxy, final boolean canSignIn) {
        if (!account.isAuthorized()) {
            if (canSignIn) {
                return authorizeAndRun(proxy/*, anAuthCompletion*/);

            } else /*if (anAuthCompletion != null)*/ {
                //anAuthCompletion.onCompleted(null, new Authorizer.AuthError(Authorizer.AuthError.Reason.NotAuthorized, null));
                Error error = new Authorizer.AuthError(Authorizer.AuthError.Reason.NotAuthorized, null);
                return Single.error(error);
            }

        } else {
            ServiceCommand serviceCommand = proxy.getServiceCommand();
            account.signCommand(serviceCommand);
            return commandRunner.run(serviceCommand);
        }
    }

    protected Single<ServiceCommand> authorizeAndRun(final ServiceCommandProxy proxy) {
        return authorize().flatMap(new Function<AuthCredentials, SingleSource<? extends ServiceCommand>>() {
            @Override
            public SingleSource<ServiceCommand> apply(AuthCredentials authCredentials) throws Exception {
                return runCommand(proxy, false);
            }
        });

//        authorize(new Authorizer.AuthorizerCompletion() {
//            @Override
//            public void onFinished(AuthCredentials credentials, Authorizer.AuthError error) {
//                if (error == null) {
//                    runCommand(proxy, false);
//
//                } else if (anAuthCompletion != null) {
//                    anAuthCompletion.onCompleted(null, error);
//                }
//            }
//        });
    }

    protected Single<AuthCredentials> authorize() {
        startAuthThreadIfNeeded();
        return account.authorize().subscribeOn(AndroidSchedulers.from(authThread.getLooper()));

//        runAsync(new Runnable() {
//            @Override
//            public void run() {
//                account.authorize(completion);
//            }
//        });
    }

//    private Handler getAuthHandler() {
//        if (authHandler == null) {
//            createAuthHandler();
//        }
//
//        return authHandler;
//    }

    private void startAuthThreadIfNeeded() {
        if (authThread != null) {
            authThread = new HandlerThread("SimpleService Auth Thread");
            authThread.start();
        }
    }

    public void cancel(ServiceCommand cmd) {
        commandRunner.cancel(cmd);
    }

//    private void runAsync(Runnable runnable) {
//        HandlerTools.runOnHandlerThread(getAuthHandler(), runnable);
//    }
}
