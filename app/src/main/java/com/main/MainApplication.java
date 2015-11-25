package com.main;

import com.example.alexeyglushkov.authcachemanager.AccountCacheStore;
import com.example.alexeyglushkov.authorization.Api.Foursquare2Api;
import com.example.alexeyglushkov.authorization.Auth.Account;
import com.example.alexeyglushkov.authorization.Auth.Authorizer;
import com.example.alexeyglushkov.authorization.Auth.SimpleAccount;
import com.example.alexeyglushkov.authorization.OAuth.OAuthAuthorizerBuilder;
import com.example.alexeyglushkov.authorization.OAuth.OAuthWebClient;
import com.example.alexeyglushkov.authtaskmanager.ServiceTaskProvider;
import com.example.alexeyglushkov.authtaskmanager.ServiceTaskRunner;
import com.example.alexeyglushkov.taskmanager.task.SimpleTask;
import com.example.alexeyglushkov.taskmanager.task.Task;
import com.rssclient.model.RssStorage;
import com.example.alexeyglushkov.taskmanager.task.SimpleTaskManager;
import com.example.alexeyglushkov.taskmanager.task.TaskManager;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import java.io.File;

public class MainApplication extends Application {
    public static final String CALLBACK_URL = "http://localhost:9000/";

    public enum Network {
        Foursquare;

        public Network fromInt(int i) {
            return values()[i];
        }
    }

    AccountCacheStore accountStore;
    TaskManager taskManager;
    RssStorage rssStorage;

    public MainApplication() {
        super();
        taskManager = new SimpleTaskManager(10);
        rssStorage = new RssStorage("RssStorage");
        loadAccountStore();
    }

    public RssStorage getRssStorage() {
        return rssStorage;
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }

    public AccountCacheStore getAccountStore() {
        return accountStore;
    }

    // Network settings and authorizers

    public void loadAccountStore() {
        final Task loadAccountTask = new SimpleTask() {
            @Override
            public void startTask() {
                File authDir = getDir("AuthFolder", Context.MODE_PRIVATE);
                AccountCacheStore store = new AccountCacheStore(authDir);
                store.load();

                for (Account acc : store.getAccounts()) {
                    if (acc.getServiceType() == Network.Foursquare.ordinal()) {
                        acc.setAuthorizer(getFoursquareAuthorizer());
                        acc.setAuthCredentialStore(getAccountStore());
                    }
                }

                getPrivate().setTaskUserData(store);
                getPrivate().handleTaskCompletion();
            }
        };

        loadAccountTask.setTaskCallback(new Task.Callback() {
            @Override
            public void onCompleted(boolean cancelled) {
                MainApplication.this.accountStore = (AccountCacheStore)loadAccountTask.getTaskUserData();
            }
        });

        taskManager.addTask(loadAccountTask);
    }

    public Account createFoursquareAccount() {
        Authorizer authorizer = getFoursquareAuthorizer();
        Account account = new SimpleAccount(Network.Foursquare.ordinal());
        account.setAuthorizer(authorizer);
        account.setAuthCredentialStore(getAccountStore());

        return account;
    }

    @NonNull
    private Authorizer getFoursquareAuthorizer() {
        String apiKey = "FEGFXJUFANVVDHVSNUAMUKTTXCP1AJQD53E33XKJ44YP1S4I";
        String apiSecret = "AYWKUL5SWPNC0CTQ202QXRUG2NLZYXMRA34ZSDW4AUYBG2RC";

        Authorizer authorizer = new OAuthAuthorizerBuilder()
                .apiKey(apiKey)
                .apiSecret(apiSecret)
                .callback(CALLBACK_URL)
                .build(new Foursquare2Api());
        authorizer.setServiceCommandProvider(new ServiceTaskProvider());
        authorizer.setServiceCommandRunner(new ServiceTaskRunner(getTaskManager(), "authorizerId"));
        return authorizer;
    }
}
