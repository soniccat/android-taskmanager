package com.example.alexeyglushkov.wordteacher.main

import android.content.Context

import com.example.alexeyglushkov.authcachemanager.AccountCacheStore
import com.example.alexeyglushkov.authorization.Api.Foursquare2Api
import com.example.alexeyglushkov.authorization.Auth.Account
import com.example.alexeyglushkov.authorization.Auth.AccountStore
import com.example.alexeyglushkov.authorization.Auth.Authorizer
import com.example.alexeyglushkov.authorization.Auth.ServiceCommandRunner
import com.example.alexeyglushkov.authorization.Auth.SimpleAccount
import com.example.alexeyglushkov.authorization.OAuth.OAuth20Authorizer
import com.example.alexeyglushkov.authorization.OAuth.OAuthAuthorizerBuilder
import com.example.alexeyglushkov.authorization.OAuth.OAuthWebClient
import com.example.alexeyglushkov.authtaskmanager.ServiceTaskProvider
import com.example.alexeyglushkov.authtaskmanager.ServiceTaskRunner
import com.example.alexeyglushkov.cachemanager.Storage
import com.example.alexeyglushkov.cachemanager.disk.DiskStorage
import com.example.alexeyglushkov.quizletservice.QuizletRepository
import com.example.alexeyglushkov.quizletservice.QuizletService
import com.example.alexeyglushkov.quizletservice.auth.QuizletApi2
import com.example.alexeyglushkov.quizletservice.tasks.QuizletServiceTaskProvider
import com.example.alexeyglushkov.taskmanager.task.SimpleTaskManager
import com.example.alexeyglushkov.taskmanager.task.StackTaskProvider
import com.example.alexeyglushkov.taskmanager.task.TaskManager
import com.example.alexeyglushkov.wordteacher.authorization.AuthActivityProxy
import com.example.alexeyglushkov.wordteacher.model.Course
import com.example.alexeyglushkov.wordteacher.model.CourseHolder

import java.io.File

import javax.inject.Named

import dagger.Module
import dagger.Provides

@Module(includes = [ContextModule::class])
class MainApplicationModule {

    @Provides
    @MainScope
    internal fun storage(@Named("appContext") context: Context): Storage {
        val cacheDir = context.getDir("ServiceCache", Context.MODE_PRIVATE)
        return DiskStorage(cacheDir)
    }

    @Provides
    @MainScope
    internal fun taskManager(): TaskManager {
        return SimpleTaskManager(10)
    }

    @Provides
    @MainScope
    internal fun webClient(): OAuthWebClient {
        return AuthActivityProxy()
    }

    @Provides
    @MainScope
    internal fun accountStore(@Named("appContext") context: Context): AccountStore {
        val authDir = context.getDir("AuthFolder", Context.MODE_PRIVATE)
        return AccountCacheStore(authDir)
    }

    @Provides
    @MainScope
    internal fun quizletService(accountStore: AccountStore, taskManager: TaskManager): QuizletService {
        try {
            val quizletAccount = Networks.getAccount(Networks.Network.Quizlet, accountStore)
            val quizletCommandProvider = QuizletServiceTaskProvider()

            val id = Integer.toString(quizletAccount.serviceType)
            val serviceCommandRunner = ServiceTaskRunner(taskManager, id)

            return QuizletService(quizletAccount, quizletCommandProvider, serviceCommandRunner)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

    }

    @Provides
    @MainScope
    internal fun quizletRepository(service: QuizletService, storage: Storage): QuizletRepository {
        return QuizletRepository(service, storage)
    }

    @Provides
    @MainScope
    @Named("quizlet")
    fun quizletAccount(accountStore: AccountStore, @Named("quizlet") authorizer: Authorizer): Account {
        val account = SimpleAccount(Networks.Network.Quizlet.ordinal)
        account.authorizer = authorizer
        account.setAuthCredentialStore(accountStore)

        return account
    }

    @Provides
    @MainScope
    @Named("quizlet")
    fun quizletAuthorizer(@Named("auth") taskProvider: ServiceTaskProvider,
                          @Named("auth") taskRunner: ServiceTaskRunner,
                          authWebClient: OAuthWebClient): Authorizer {
        val apiKey = "9zpZ2myVfS"
        val apiSecret = "bPHS9xz2sCXWwq5ddcWswG"

        val authorizer = OAuthAuthorizerBuilder()
                .apiKey(apiKey)
                .apiSecret(apiSecret)
                .callback(Networks.CALLBACK_URL)
                .build(QuizletApi2()) as OAuth20Authorizer
        authorizer.setServiceCommandProvider(taskProvider)
        authorizer.setServiceCommandRunner(taskRunner)
        authorizer.setWebClient(authWebClient)

        return authorizer
    }

    @Provides
    @MainScope
    @Named("foursquare")
    fun foursquareAccount(accountStore: AccountStore, @Named("foursquare") authorizer: Authorizer): Account {
        val account = SimpleAccount(Networks.Network.Foursquare.ordinal)
        account.authorizer = authorizer
        account.setAuthCredentialStore(accountStore)

        return account
    }

    @Provides
    @MainScope
    @Named("foursquare")
    fun foursquareAuthorizer(@Named("auth") taskProvider: ServiceTaskProvider,
                             @Named("auth") taskRunner: ServiceTaskRunner,
                             authWebClient: OAuthWebClient): Authorizer {
        val apiKey = "FEGFXJUFANVVDHVSNUAMUKTTXCP1AJQD53E33XKJ44YP1S4I"
        val apiSecret = "AYWKUL5SWPNC0CTQ202QXRUG2NLZYXMRA34ZSDW4AUYBG2RC"

        val authorizer = OAuthAuthorizerBuilder()
                .apiKey(apiKey)
                .apiSecret(apiSecret)
                .callback(Networks.CALLBACK_URL)
                .build(Foursquare2Api()) as OAuth20Authorizer
        authorizer.setServiceCommandProvider(taskProvider)
        authorizer.setServiceCommandRunner(taskRunner)
        authorizer.setWebClient(authWebClient)

        return authorizer
    }

    @Provides
    @MainScope
    @Named("auth")
    fun authServiceTaskProvider(): ServiceTaskProvider {
        return ServiceTaskProvider()
    }

    @Provides
    @MainScope
    @Named("auth")
    fun authServiceTaskRunner(taskManager: TaskManager): ServiceTaskRunner {
        return ServiceTaskRunner(taskManager, "authorizerId")
    }

    @Provides
    @MainScope
    fun createCourseHolder(@Named("appContext") context: Context, taskManager: TaskManager): CourseHolder {
        val taskProvider = StackTaskProvider(true, "CourseHolderTaskProvider", taskManager.scope);
        taskManager.addTaskProvider(taskProvider)

        val authDir = context.getDir("CourseHolder", Context.MODE_PRIVATE)
        return CourseHolder(authDir, taskProvider)
    }

    //    private void createDropboxService() throws Exception {
    //        DropboxAccount dropboxAccount = (DropboxAccount)Networks.getAccount(Networks.Network.Dropbox);
    //        DropboxCommandProvider commandProvider = new DropboxServiceTaskProvider();
    //
    //        String id = Integer.toString(dropboxAccount.getServiceType());
    //        ServiceCommandRunner serviceCommandRunner = new ServiceTaskRunner(getTaskManager(), id);
    //
    //        Storage storage = new PreferenceStorage("DropboxServicePref", getContextProvider());
    //
    //        dropboxService = new DropboxService(dropboxAccount, commandProvider, serviceCommandRunner, storage);
    //        dropboxService.setCallback(new DropboxService.Callback() {
    //            @Override
    //            public void merge(@NonNull File localFile, @NonNull DropboxAPI.Entry dropboxEntry, DropboxCommandProvider.MergeCompletion completion) {
    //                MainApplication.this.merge(localFile, dropboxEntry, completion);
    //            }
    //        });
    //    }
}
