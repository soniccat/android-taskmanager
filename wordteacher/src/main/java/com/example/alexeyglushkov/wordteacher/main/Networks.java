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

    public static Account getAccount(Network network, AccountStore accountStore) {
        Assert.assertNotNull("accountStore must exists", accountStore);

        List<Account> accounts = accountStore.getAccounts(network.ordinal());
        Account account = null;
        if (accounts.size() > 0) {
            account = accounts.get(0);
        } else {
            account = createAccount(network);
        }

        return account;
    }

    public static void restoreAuthorizer(Account acc) {
        MainApplication.MainComponent component = MainApplication.Companion.getInstance().getComponent();
        if (acc.getServiceType() == Networks.Network.Foursquare.ordinal()) {
            acc.setAuthorizer(component.getFoursquareAuthorizer());

        } else if (acc.getServiceType() == Network.Quizlet.ordinal()) {
            acc.setAuthorizer(component.getQuizletAuthorizer());
        }

//        } else if (acc.getServiceType() == Network.Dropbox.ordinal()) {
//            acc.setAuthorizer(Networks.getDropboxAuthorizer());
//        }
    }

    @NonNull
    public static Account createAccount(Network network) {
        if (network == Network.Foursquare) {
            return MainApplication.Companion.getInstance().getComponent().getFoursquareAccount();

        } else if (network == Network.Quizlet) {
            return MainApplication.Companion.getInstance().getComponent().getQuizletAccount();
        }
//        } else if (network == Network.Dropbox) {
//            return createDropboxAccount();
//        }

        Assert.assertTrue(false);
        return null;
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
