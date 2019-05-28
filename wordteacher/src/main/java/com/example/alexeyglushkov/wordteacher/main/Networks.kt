package com.example.alexeyglushkov.wordteacher.main

import com.example.alexeyglushkov.authorization.Api.Foursquare2Api
import com.example.alexeyglushkov.quizletservice.auth.QuizletApi2
import com.example.alexeyglushkov.authorization.Auth.Account
import com.example.alexeyglushkov.authorization.Auth.AccountStore
import com.example.alexeyglushkov.authorization.Auth.Authorizer
import com.example.alexeyglushkov.authorization.Auth.SimpleAccount
import com.example.alexeyglushkov.authorization.OAuth.OAuth20Authorizer
import com.example.alexeyglushkov.authorization.OAuth.OAuthAuthorizerBuilder
import com.example.alexeyglushkov.authorization.OAuth.OAuthWebClient
import com.example.alexeyglushkov.authtaskmanager.ServiceTaskProvider
import com.example.alexeyglushkov.authtaskmanager.ServiceTaskRunner
import com.example.alexeyglushkov.taskmanager.task.TaskManager

import org.junit.Assert
import java.lang.RuntimeException

/**
 * Created by alexeyglushkov on 25.11.15.
 */

// TODO: avoid method duplication for every network, move to classes
object Networks {
    //TODO: it could be different for each network
    val CALLBACK_URL = "http://gaolife.blogspot.ru"

    enum class Network {
        None,
        Foursquare,
        Quizlet,
        Dropbox;


        companion object {

            fun fromInt(i: Int): Network {
                return values()[i]
            }
        }
    }

    fun getAccount(network: Network, accountStore: AccountStore): Account {
        Assert.assertNotNull("accountStore must exists", accountStore)

        val accounts = accountStore.getAccounts(network.ordinal)
        val account: Account
        if (accounts.size > 0) {
            account = accounts[0]
        } else {
            account = createAccount(network)
        }

        return account
    }

    fun restoreAuthorizer(acc: Account) {
        val component = MainApplication.instance.component
        if (acc.serviceType == Networks.Network.Foursquare.ordinal) {
            acc.authorizer = component.foursquareAuthorizer

        } else if (acc.serviceType == Network.Quizlet.ordinal) {
            acc.authorizer = component.quizletAuthorizer
        }

        //        } else if (acc.getServiceType() == Network.Dropbox.ordinal()) {
        //            acc.setAuthorizer(Networks.getDropboxAuthorizer());
        //        }
    }

    fun createAccount(network: Network): Account {
        if (network == Network.Foursquare) {
            return MainApplication.instance.component.foursquareAccount

        } else if (network == Network.Quizlet) {
            return MainApplication.instance.component.quizletAccount
        }
        //        } else if (network == Network.Dropbox) {
        //            return createDropboxAccount();
        //        }

        throw RuntimeException("Networks.createAccount wrong netowork " + network)
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
