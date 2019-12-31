package com.main

import com.example.alexeyglushkov.authorization.Api.Foursquare2Api
import com.example.alexeyglushkov.authorization.Auth.Account
import com.example.alexeyglushkov.authorization.Auth.AccountStore
import com.example.alexeyglushkov.authorization.Auth.Authorizer
import com.example.alexeyglushkov.authorization.Auth.SimpleAccount
import com.example.alexeyglushkov.authorization.OAuth.OAuth20Authorizer
import com.example.alexeyglushkov.authorization.OAuth.OAuthAuthorizerBuilder
import com.example.alexeyglushkov.authorization.OAuth.OAuthWebClient
import com.example.alexeyglushkov.authtaskmanager.ServiceTaskProvider
import com.example.alexeyglushkov.authtaskmanager.ServiceTaskRunner
import com.example.alexeyglushkov.quizletservice.auth.QuizletApi2
import com.example.alexeyglushkov.taskmanager.task.TaskManager
import com.main.Networks.Network
import org.junit.Assert
import java.lang.RuntimeException

/**
 * Created by alexeyglushkov on 25.11.15.
 */
// TODO: avoid method duplication for every network, move to classes
object Networks {
    //TODO: it could be different for each network
    const val CALLBACK_URL = "http://gaolife.blogspot.ru"

    val authWebClient: OAuthWebClient
        get() = MainApplication.instance.authWebClient

    val taskManager: TaskManager
        get() = MainApplication.instance.taskManager

    val accountStore: AccountStore
        get() = MainApplication.instance.accountStore

    fun getAccount(serviceType: Int): Account {
        Assert.assertNotNull("accountStore must exists", accountStore)
        val accounts = accountStore.getAccounts(serviceType)
        return  if (accounts.size > 0) {
            accounts[0]
        } else {
            createAccount(Network.Quizlet)
        }
    }

    fun restoreAuthorizer(acc: Account) {
        if (acc.serviceType == Network.Foursquare.ordinal) {
            acc.authorizer = foursquareAuthorizer
        } else if (acc.serviceType == Network.Quizlet.ordinal) {
            acc.authorizer = quizletAuthorizer
        }
    }

    fun createAccount(network: Network): Account {
        return when (network) {
            Network.Foursquare -> createFoursquareAccount()
            Network.Quizlet -> createQuizletAccount()
            Network.None -> throw IllegalArgumentException("Invalid network")
        }
    }

    fun createFoursquareAccount(): Account {
        val authorizer = foursquareAuthorizer
        val account: Account = SimpleAccount(Network.Foursquare.ordinal)
        account.authorizer = authorizer
        account.setAuthCredentialStore(accountStore)
        return account
    }

    val foursquareAuthorizer: Authorizer
        get() {
            val apiKey = "FEGFXJUFANVVDHVSNUAMUKTTXCP1AJQD53E33XKJ44YP1S4I"
            val apiSecret = "AYWKUL5SWPNC0CTQ202QXRUG2NLZYXMRA34ZSDW4AUYBG2RC"
            val authorizer = OAuthAuthorizerBuilder()
                    .apiKey(apiKey)
                    .apiSecret(apiSecret)
                    .callback(CALLBACK_URL)
                    .build(Foursquare2Api()) as OAuth20Authorizer
            authorizer.setServiceCommandProvider(ServiceTaskProvider())
            authorizer.setServiceCommandRunner(ServiceTaskRunner(taskManager, "authorizerId"))
            authorizer.webClient = authWebClient
            return authorizer
        }

    fun createQuizletAccount(): Account {
        val authorizer = quizletAuthorizer
        val account: Account = SimpleAccount(Network.Quizlet.ordinal)
        account.authorizer = authorizer
        account.setAuthCredentialStore(accountStore)
        return account
    }

    val quizletAuthorizer: Authorizer
        get() {
            val apiKey = "9zpZ2myVfS"
            val apiSecret = "bPHS9xz2sCXWwq5ddcWswG"
            val authorizer = OAuthAuthorizerBuilder()
                    .apiKey(apiKey)
                    .apiSecret(apiSecret)
                    .callback(CALLBACK_URL)
                    .build(QuizletApi2()) as OAuth20Authorizer
            authorizer.setServiceCommandProvider(ServiceTaskProvider())
            authorizer.setServiceCommandRunner(ServiceTaskRunner(taskManager, "authorizerId"))
            authorizer.webClient = authWebClient
            return authorizer
        }

    enum class Network {
        None, Foursquare, Quizlet;
    }
}