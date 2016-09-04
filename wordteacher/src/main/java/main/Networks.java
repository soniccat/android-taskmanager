package main;

import android.content.Context;
import android.os.DropBoxManager;
import android.support.annotation.NonNull;

import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;
import com.example.alexeyglushkov.authorization.Api.Foursquare2Api;
import com.example.alexeyglushkov.dropboxservice.ContextProvider;
import com.example.alexeyglushkov.dropboxservice.DropboxAccount;
import com.example.alexeyglushkov.dropboxservice.DropboxAuthorizer;
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

import junit.framework.Assert;

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

    public static OAuthWebClient getAuthWebClient() {
        return MainApplication.instance.getAuthWebClient();
    }

    public static TaskManager getTaskManager() {
        return MainApplication.instance.getTaskManager();
    }

    public static AccountStore getAccountStore() {
        return MainApplication.instance.getAccountStore();
    }

    public static Account getAccount(Network network) {
        Assert.assertNotNull("accountStore must exists", getAccountStore());

        List<Account> accounts = getAccountStore().getAccounts(network.ordinal());
        Account account = null;
        if (accounts.size() > 0) {
            account = accounts.get(0);
        } else {
            account = createAccount(network);
        }

        return account;
    }

    public static void restoreAuthorizer(Account acc) {
        if (acc.getServiceType() == Networks.Network.Foursquare.ordinal()) {
            acc.setAuthorizer(Networks.getFoursquareAuthorizer());

        } else if (acc.getServiceType() == Network.Quizlet.ordinal()) {
            acc.setAuthorizer(Networks.getQuizletAuthorizer());

        } else if (acc.getServiceType() == Network.Dropbox.ordinal()) {
            acc.setAuthorizer(Networks.getDropboxAuthorizer());
        }
    }

    @NonNull
    public static Account createAccount(Network network) {
        if (network == Network.Foursquare) {
            return createFoursquareAccount();

        } else if (network == Network.Quizlet) {
            return createQuizletAccount();

        } else if (network == Network.Dropbox) {
            return createDropboxAccount();
        }

        Assert.assertTrue(false);
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

        OAuth20Authorizer authorizer = (OAuth20Authorizer)new OAuthAuthorizerBuilder()
                .apiKey(apiKey)
                .apiSecret(apiSecret)
                .callback(CALLBACK_URL)
                .build(new Foursquare2Api());
        authorizer.setServiceCommandProvider(new ServiceTaskProvider());
        authorizer.setServiceCommandRunner(new ServiceTaskRunner(getTaskManager(), "authorizerId"));
        authorizer.setWebClient(getAuthWebClient());

        return authorizer;
    }

    public static Account createQuizletAccount() {
        Authorizer authorizer = getQuizletAuthorizer();
        Account account = new SimpleAccount(Network.Quizlet.ordinal());
        account.setAuthorizer(authorizer);
        account.setAuthCredentialStore(getAccountStore());

        return account;
    }

    @NonNull
    public static Authorizer getQuizletAuthorizer() {
        String apiKey = "9zpZ2myVfS";
        String apiSecret = "bPHS9xz2sCXWwq5ddcWswG";

        OAuth20Authorizer authorizer = (OAuth20Authorizer)new OAuthAuthorizerBuilder()
                .apiKey(apiKey)
                .apiSecret(apiSecret)
                .callback(CALLBACK_URL)
                .build(new QuizletApi2());
        authorizer.setServiceCommandProvider(new ServiceTaskProvider());
        authorizer.setServiceCommandRunner(new ServiceTaskRunner(getTaskManager(), "authorizerId"));
        authorizer.setWebClient(getAuthWebClient());

        return authorizer;
    }

    public static Account createDropboxAccount() {
        Authorizer authorizer = getDropboxAuthorizer();
        DropboxAccount account = new DropboxAccount(Network.Dropbox.ordinal());
        account.setAuthorizer(authorizer);
        account.setAuthCredentialStore(getAccountStore());

        // put account to be able to call onResume
        account.store();

        return account;
    }

    public static Authorizer getDropboxAuthorizer() {
        AppKeyPair appKeyPair = new AppKeyPair("0zq9vxe6h5u32vv", "6wbv48j08mz5aa9");
        AndroidAuthSession session = new AndroidAuthSession(appKeyPair);

        DropboxAuthorizer authorizer = new DropboxAuthorizer(session, MainApplication.getContextProvider());

        return authorizer;
    }
}
