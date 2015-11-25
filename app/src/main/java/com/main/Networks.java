package com.main;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.example.alexeyglushkov.authcachemanager.AccountCacheStore;
import com.example.alexeyglushkov.authorization.Api.Foursquare2Api;
import com.example.alexeyglushkov.authorization.Auth.Account;
import com.example.alexeyglushkov.authorization.Auth.Authorizer;
import com.example.alexeyglushkov.authorization.Auth.SimpleAccount;
import com.example.alexeyglushkov.authorization.OAuth.OAuthAuthorizerBuilder;
import com.example.alexeyglushkov.authtaskmanager.ServiceTaskProvider;
import com.example.alexeyglushkov.authtaskmanager.ServiceTaskRunner;
import com.example.alexeyglushkov.taskmanager.task.TaskManager;

/**
 * Created by alexeyglushkov on 25.11.15.
 */
public class Networks {
    public static final String CALLBACK_URL = "http://localhost:9000/";

    public enum Network {
        None,
        Foursquare;

        public static Network fromInt(int i) {
            return values()[i];
        }
    }

    public static TaskManager getTaskManager() {
        return MainApplication.instance.getTaskManager();
    }

    public static AccountCacheStore getAccountStore() {
        return MainApplication.instance.getAccountStore();
    }

    public static void restoreAuthorizer(Account acc) {
        if (acc.getServiceType() == Networks.Network.Foursquare.ordinal()) {
            acc.setAuthorizer(Networks.getFoursquareAuthorizer());
        }
    }

    public static Account createAccount(Network network) {
        if (network == Network.Foursquare) {
            return createFoursquareAccount();
        }

        return null;
    }

    public static Account createFoursquareAccount() {
        Authorizer authorizer = getFoursquareAuthorizer();
        Account account = new SimpleAccount(Network.Foursquare.ordinal());
        account.setAuthorizer(authorizer);
        account.setAuthCredentialStore(getAccountStore());

        return account;
    }

    @NonNull
    public static Authorizer getFoursquareAuthorizer() {
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
