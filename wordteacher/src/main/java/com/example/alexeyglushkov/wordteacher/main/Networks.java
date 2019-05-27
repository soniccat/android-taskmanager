package com.example.alexeyglushkov.wordteacher.main;

import androidx.annotation.NonNull;

import com.example.alexeyglushkov.authorization.Api.Foursquare2Api;
import com.example.alexeyglushkov.quizletservice.auth.QuizletApi2;
import com.example.alexeyglushkov.authorization.Auth.Account;
import com.example.alexeyglushkov.authorization.Auth.AccountStore;
import com.example.alexeyglushkov.authorization.Auth.Authorizer;
import com.example.alexeyglushkov.authorization.Auth.SimpleAccount;
import com.example.alexeyglushkov.authorization.OAuth.OAuth20Authorizer;
import com.example.alexeyglushkov.authorization.OAuth.OAuthAuthorizerBuilder;
import com.example.alexeyglushkov.authorization.OAuth.OAuthWebClient;
import com.example.alexeyglushkov.authtaskmanager.ServiceTaskProvider;
import com.example.alexeyglushkov.authtaskmanager.ServiceTaskRunner;
import com.example.alexeyglushkov.taskmanager.task.TaskManager;

import org.junit.Assert;

import java.util.List;

/**
 * Created by alexeyglushkov on 25.11.15.
 */

// TODO: avoid method duplication for every network, move to classes
public class Networks {
    //TODO: it could be different for each network
    public static final String CALLBACK_URL = "http://gaolife.blogspot.ru";

    public enum Network {
        None,
        Foursquare,
        Quizlet,
        Dropbox;

        public static Network fromInt(int i) {
            return values()[i];
        }
    }

    public static Account getAccount(Network network, AccountStore accountStore, TaskManager taskManager, OAuthWebClient authWebClient) throws Exception {
        Assert.assertNotNull("accountStore must exists", accountStore);

        List<Account> accounts = accountStore.getAccounts(network.ordinal());
        Account account = null;
        if (accounts.size() > 0) {
            account = accounts.get(0);
        } else {
            account = createAccount(network, accountStore, taskManager, authWebClient);
        }

        return account;
    }

    public static void restoreAuthorizer(Account acc, TaskManager taskManager, OAuthWebClient authWebClient) {
        if (acc.getServiceType() == Networks.Network.Foursquare.ordinal()) {
            acc.setAuthorizer(Networks.getFoursquareAuthorizer(taskManager, authWebClient));

        } else if (acc.getServiceType() == Network.Quizlet.ordinal()) {
            acc.setAuthorizer(Networks.getQuizletAuthorizer(taskManager, authWebClient));
        }

//        } else if (acc.getServiceType() == Network.Dropbox.ordinal()) {
//            acc.setAuthorizer(Networks.getDropboxAuthorizer());
//        }
    }

    @NonNull
    public static Account createAccount(Network network, AccountStore accountStore, TaskManager taskManager, OAuthWebClient authWebClient) throws Exception {
        if (network == Network.Foursquare) {
            return createFoursquareAccount(accountStore, taskManager, authWebClient);

        } else if (network == Network.Quizlet) {
            return createQuizletAccount(accountStore, taskManager, authWebClient);
        }
//        } else if (network == Network.Dropbox) {
//            return createDropboxAccount();
//        }

        Assert.assertTrue(false);
        return null;
    }

    public static Account createFoursquareAccount(AccountStore accountStore, TaskManager taskManager, OAuthWebClient authWebClient) {
        Authorizer authorizer = getFoursquareAuthorizer(taskManager, authWebClient);
        Account account = new SimpleAccount(Network.Foursquare.ordinal());
        account.setAuthorizer(authorizer);
        account.setAuthCredentialStore(accountStore);

        return account;
    }

    @NonNull
    public static Authorizer getFoursquareAuthorizer(TaskManager taskManager, OAuthWebClient authWebClient) {
        String apiKey = "FEGFXJUFANVVDHVSNUAMUKTTXCP1AJQD53E33XKJ44YP1S4I";
        String apiSecret = "AYWKUL5SWPNC0CTQ202QXRUG2NLZYXMRA34ZSDW4AUYBG2RC";

        OAuth20Authorizer authorizer = (OAuth20Authorizer)new OAuthAuthorizerBuilder()
                .apiKey(apiKey)
                .apiSecret(apiSecret)
                .callback(CALLBACK_URL)
                .build(new Foursquare2Api());
        authorizer.setServiceCommandProvider(new ServiceTaskProvider());
        authorizer.setServiceCommandRunner(new ServiceTaskRunner(taskManager, "authorizerId"));
        authorizer.setWebClient(authWebClient);

        return authorizer;
    }

    public static Account createQuizletAccount(AccountStore accountStore, TaskManager taskManager, OAuthWebClient authWebClient) {
        Authorizer authorizer = getQuizletAuthorizer(taskManager, authWebClient);
        Account account = new SimpleAccount(Network.Quizlet.ordinal());
        account.setAuthorizer(authorizer);
        account.setAuthCredentialStore(accountStore);

        return account;
    }

    @NonNull
    public static Authorizer getQuizletAuthorizer(TaskManager taskManager, OAuthWebClient authWebClient) {
        String apiKey = "9zpZ2myVfS";
        String apiSecret = "bPHS9xz2sCXWwq5ddcWswG";

        OAuth20Authorizer authorizer = (OAuth20Authorizer)new OAuthAuthorizerBuilder()
                .apiKey(apiKey)
                .apiSecret(apiSecret)
                .callback(CALLBACK_URL)
                .build(new QuizletApi2());
        authorizer.setServiceCommandProvider(new ServiceTaskProvider());
        authorizer.setServiceCommandRunner(new ServiceTaskRunner(taskManager, "authorizerId"));
        authorizer.setWebClient(authWebClient);

        return authorizer;
    }

//    public static Account createDropboxAccount() throws Exception {
//        Authorizer authorizer = getDropboxAuthorizer();
//        DropboxAccount account = new DropboxAccount(Network.Dropbox.ordinal());
//        account.setAuthorizer(authorizer);
//        account.setAuthCredentialStore(getAccountStore());
//
//        // put account to be able to call onResume
//        account.store();
//
//        return account;
//    }
//
//    public static Authorizer getDropboxAuthorizer() {
//        AppKeyPair appKeyPair = new AppKeyPair("0zq9vxe6h5u32vv", "6wbv48j08mz5aa9");
//        AndroidAuthSession session = new AndroidAuthSession(appKeyPair);
//
//        DropboxAuthorizer authorizer = new DropboxAuthorizer(session, MainApplication.getContextProvider());
//
//        return authorizer;
//    }
}
