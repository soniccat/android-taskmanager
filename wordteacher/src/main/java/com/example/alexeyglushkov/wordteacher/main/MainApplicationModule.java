package com.example.alexeyglushkov.wordteacher.main;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.alexeyglushkov.authcachemanager.AccountCacheStore;
import com.example.alexeyglushkov.authorization.Api.Foursquare2Api;
import com.example.alexeyglushkov.authorization.Auth.Account;
import com.example.alexeyglushkov.authorization.Auth.AccountStore;
import com.example.alexeyglushkov.authorization.Auth.Authorizer;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommandRunner;
import com.example.alexeyglushkov.authorization.Auth.SimpleAccount;
import com.example.alexeyglushkov.authorization.OAuth.OAuth20Authorizer;
import com.example.alexeyglushkov.authorization.OAuth.OAuthAuthorizerBuilder;
import com.example.alexeyglushkov.authorization.OAuth.OAuthWebClient;
import com.example.alexeyglushkov.authtaskmanager.ServiceTaskProvider;
import com.example.alexeyglushkov.authtaskmanager.ServiceTaskRunner;
import com.example.alexeyglushkov.cachemanager.Storage;
import com.example.alexeyglushkov.cachemanager.disk.DiskStorage;
import com.example.alexeyglushkov.quizletservice.QuizletRepository;
import com.example.alexeyglushkov.quizletservice.QuizletService;
import com.example.alexeyglushkov.quizletservice.auth.QuizletApi2;
import com.example.alexeyglushkov.quizletservice.tasks.QuizletServiceTaskProvider;
import com.example.alexeyglushkov.taskmanager.task.SimpleTaskManager;
import com.example.alexeyglushkov.taskmanager.task.TaskManager;
import com.example.alexeyglushkov.wordteacher.authorization.AuthActivityProxy;

import java.io.File;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module(includes = ContextModule.class)
public class MainApplicationModule {

    @Provides
    @MainScope
    @NonNull
    Storage storage(@Named("appContext") Context context) {
        File cacheDir = context.getDir("ServiceCache", Context.MODE_PRIVATE);
        return new DiskStorage(cacheDir);
    }

    @Provides
    @MainScope
    @NonNull
    TaskManager taskManager() {
        return new SimpleTaskManager(10);
    }

    @Provides
    @MainScope
    @NonNull
    OAuthWebClient webClient() {
        return new AuthActivityProxy();
    }

    @Provides
    @MainScope
    @NonNull
    AccountStore accountStore(@Named("appContext") Context context) {
        File authDir = context.getDir("AuthFolder", Context.MODE_PRIVATE);
        return new AccountCacheStore(authDir);
    }

    @Provides
    @MainScope
    @NonNull
    QuizletService quizletService(Storage storage, AccountStore accountStore, TaskManager taskManager) {
        try {
            Account quizletAccount = Networks.getAccount(Networks.Network.Quizlet, accountStore);
            QuizletServiceTaskProvider quizletCommandProvider = new QuizletServiceTaskProvider(storage);

            String id = Integer.toString(quizletAccount.getServiceType());
            ServiceCommandRunner serviceCommandRunner = new ServiceTaskRunner(taskManager, id);

            return new QuizletService(quizletAccount, quizletCommandProvider, serviceCommandRunner);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Provides
    @MainScope
    @NonNull
    QuizletRepository quizletRepository(QuizletService service, Storage storage) {
        return new QuizletRepository(service, storage);
    }

    @Provides
    @MainScope
    @NonNull
    public @Named("quizlet") Authorizer quizletAuthorizer(TaskManager taskManager, OAuthWebClient authWebClient) {
        String apiKey = "9zpZ2myVfS";
        String apiSecret = "bPHS9xz2sCXWwq5ddcWswG";

        OAuth20Authorizer authorizer = (OAuth20Authorizer)new OAuthAuthorizerBuilder()
                .apiKey(apiKey)
                .apiSecret(apiSecret)
                .callback(Networks.CALLBACK_URL)
                .build(new QuizletApi2());
        authorizer.setServiceCommandProvider(new ServiceTaskProvider());
        authorizer.setServiceCommandRunner(new ServiceTaskRunner(taskManager, "authorizerId"));
        authorizer.setWebClient(authWebClient);

        return authorizer;
    }

    @Provides
    @MainScope
    @NonNull
    public @Named("quizlet") Account quizletAccount(AccountStore accountStore, @Named("quizlet") Authorizer authorizer) {
        Account account = new SimpleAccount(Networks.Network.Quizlet.ordinal());
        account.setAuthorizer(authorizer);
        account.setAuthCredentialStore(accountStore);

        return account;
    }

    @Provides
    @MainScope
    @NonNull
    public @Named("foursquare") Authorizer foursquareAuthorizer(TaskManager taskManager, OAuthWebClient authWebClient) {
        String apiKey = "FEGFXJUFANVVDHVSNUAMUKTTXCP1AJQD53E33XKJ44YP1S4I";
        String apiSecret = "AYWKUL5SWPNC0CTQ202QXRUG2NLZYXMRA34ZSDW4AUYBG2RC";

        OAuth20Authorizer authorizer = (OAuth20Authorizer)new OAuthAuthorizerBuilder()
                .apiKey(apiKey)
                .apiSecret(apiSecret)
                .callback(Networks.CALLBACK_URL)
                .build(new Foursquare2Api());
        authorizer.setServiceCommandProvider(new ServiceTaskProvider());
        authorizer.setServiceCommandRunner(new ServiceTaskRunner(taskManager, "authorizerId"));
        authorizer.setWebClient(authWebClient);

        return authorizer;
    }

    @Provides
    @MainScope
    @NonNull
    public @Named("foursquare") Account foursquareAccount(AccountStore accountStore, @Named("foursquare") Authorizer authorizer) {
        Account account = new SimpleAccount(Networks.Network.Foursquare.ordinal());
        account.setAuthorizer(authorizer);
        account.setAuthCredentialStore(accountStore);

        return account;
    }
}
