package com.example.alexeyglushkov.wordteacher.main;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import dagger.Subcomponent;
import io.reactivex.internal.functions.Functions;
import io.reactivex.schedulers.Schedulers;

//import com.dropbox.client2.DropboxAPI;
import com.example.alexeyglushkov.authcachemanager.AccountCacheStore;
import com.example.alexeyglushkov.authorization.Auth.Account;
import com.example.alexeyglushkov.authorization.Auth.AccountStore;
import com.example.alexeyglushkov.authorization.Auth.Authorizer;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommandRunner;
import com.example.alexeyglushkov.authorization.OAuth.OAuthWebClient;
import com.example.alexeyglushkov.authtaskmanager.ServiceTaskRunner;
import com.example.alexeyglushkov.cachemanager.StorageCleaner;
import com.example.alexeyglushkov.cachemanager.Storage;
import com.example.alexeyglushkov.cachemanager.SimpleStorageCleaner;
import com.example.alexeyglushkov.quizletservice.QuizletRepository;
import com.example.alexeyglushkov.tools.ContextProvider;
import com.example.alexeyglushkov.quizletservice.QuizletService;
import com.example.alexeyglushkov.quizletservice.tasks.QuizletServiceTaskProvider;
import com.example.alexeyglushkov.taskmanager.task.TaskManager;

import org.junit.Assert;

import java.io.File;

import com.example.alexeyglushkov.wordteacher.authorization.AuthActivityProxy;
import com.example.alexeyglushkov.wordteacher.model.CourseHolder;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

public class MainApplication extends Application {
    private static final String TAG = "MainApplication";

    @Inject @NonNull AccountStore accountStore;
    @Inject @NonNull OAuthWebClient authWebClient;

    private @NonNull QuizletRepository quizletRepository;
    //private @NonNull DropboxService dropboxService;
    private @NonNull CourseHolder courseHolder;

    @Inject @NonNull TaskManager taskManager;
    @Inject @NonNull Storage storage;

    public static @NonNull MainApplication instance;
    private @NonNull MainComponent component;

    //// Initialization

    @MainScope
    @dagger.Component(modules = MainApplicationModule.class)
    public interface MainComponent {
        Storage getStorage();
        TaskManager getTaskManager();
        QuizletRepository getQuizletRepozitory();
        @Named("quizlet") Authorizer getQuizletAuthorizer();
        @Named("quizlet") Account getQuizletAccount();
        @Named("foursquare") Authorizer getFoursquareAuthorizer();
        @Named("foursquare") Account getFoursquareAccount();
        void inject(MainApplication app);
    }

    public MainApplication() {
        super();
        instance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        component = DaggerMainApplication_MainComponent.builder()
                .contextModule(new ContextModule(getApplicationContext()))
                .build();
        component.inject(this);

        cleanCache();
        loadAccountStore();

        createCourseHolder();
        loadCourseHolder();
    }

    //// Events

    private void onAccountStoreLoaded() {
        try {
            quizletRepository = component.getQuizletRepozitory();
            quizletRepository.restoreOrLoad(null);
            //createDropboxService();

        } catch (Exception ex) {
            finishApp(ex);
        }
    }

    private void finishApp(Exception ex) {
        ex.printStackTrace();
        Assert.fail(ex.getMessage());
    }

    //// Actions

    public void loadAccountStore() {
        try {
            this.accountStore.load();
        } catch (Exception e) {
            Log.e(TAG, "Can't load account store");
            e.printStackTrace();
        }
        restoreAccounts((AccountCacheStore)this.accountStore);
        onAccountStoreLoaded();
    }

    private void restoreAccounts(AccountCacheStore store) {
        for (Account acc : store.getAccounts()) {
            acc.setAuthCredentialStore(store);
            Networks.restoreAuthorizer(acc);
        }
    }

    public void loadCourseHolder() {
        taskManager.addTask(courseHolder.getLoadCourseListTask());
    }

    public void cleanCache() {
        StorageCleaner cleaner = new SimpleStorageCleaner();
        cleaner.clean(getStorage())
                .subscribeOn(Schedulers.io())
                .subscribe(Functions.EMPTY_ACTION, Functions.emptyConsumer());
    }

//    private void merge(@NonNull File localFile, @NonNull DropboxAPI.Entry dropboxEntry, DropboxCommandProvider.MergeCompletion completion) {
//        File outFile;
//
//        try {
//            File tmpDir = getCacheDir();
//
//            UUID outFileName = UUID.randomUUID();
//            outFile = File.createTempFile(outFileName.toString(), "", getCacheDir());
//
//            FileMerger fileMerger = new FileObjectMerger(new CourseCodec(), new CourseMerger(), outFile);
//            DropboxFileMerger merger = new DropboxFileMerger(dropboxService.getApi(), tmpDir, fileMerger);
//
//            merger.merge(localFile, dropboxEntry, completion);
//
//        } catch (Exception ex) {
//            completion.completed(null, new Error("Merge exception", ex));
//        }
//    }

    //// Creation Methods

    private void createCourseHolder() {
        File authDir = getDir("CourseHolder", Context.MODE_PRIVATE);
        this.courseHolder = new CourseHolder(authDir);
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

    //// Getters

    @NonNull
    public static ContextProvider getContextProvider() {
        return new ContextProvider() {
            @Override
            public Context getContext() {
                return MainApplication.instance.getCurrentContext();
            }
        };
    }

    public @NonNull TaskManager getTaskManager() {
        return taskManager;
    }

    public @NonNull AccountStore getAccountStore() {
        return accountStore;
    }

    public @NonNull CourseHolder getCourseHolder() {
        return courseHolder;
    }

    public @NonNull OAuthWebClient getAuthWebClient() {
        return authWebClient;
    }

    public @NonNull
    Storage getStorage() {
        return storage;
    }

    public @NonNull
    QuizletRepository getQuizletRepository() {
        return quizletRepository;
    }

    @NonNull
    public MainComponent getComponent() {
        return component;
    }

    //    public @NonNull DropboxService getDropboxService() {
//        return dropboxService;
//    }

    // Cast Getters

    public Context getCurrentContext() {
        return getCurrentActivity();
    }

    private Activity getCurrentActivity() {
        return AuthActivityProxy.getCurrentActivity();
    }
}
