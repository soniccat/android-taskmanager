package com.example.alexeyglushkov.wordteacher.main;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.dropbox.client2.DropboxAPI;
import com.example.alexeyglushkov.authcachemanager.AccountCacheStore;
import com.example.alexeyglushkov.authorization.Auth.Account;
import com.example.alexeyglushkov.authorization.Auth.AccountStore;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommandRunner;
import com.example.alexeyglushkov.authorization.OAuth.OAuthWebClient;
import com.example.alexeyglushkov.authtaskmanager.ServiceTaskRunner;
import com.example.alexeyglushkov.cachemanager.StorageCleaner;
import com.example.alexeyglushkov.cachemanager.Storage;
import com.example.alexeyglushkov.cachemanager.disk.DiskStorageCleaner;
import com.example.alexeyglushkov.cachemanager.disk.DiskStorage;
import com.example.alexeyglushkov.tools.ContextProvider;
import com.example.alexeyglushkov.dropboxservice.DropboxAccount;
import com.example.alexeyglushkov.dropboxservice.DropboxCommandProvider;
import com.example.alexeyglushkov.dropboxservice.DropboxFileMerger;
import com.example.alexeyglushkov.dropboxservice.DropboxService;
import com.example.alexeyglushkov.dropboxservice.DropboxServiceTaskProvider;
import com.example.alexeyglushkov.dropboxservice.FileMerger;
import com.example.alexeyglushkov.dropboxservice.FileObjectMerger;
import com.example.alexeyglushkov.quizletservice.QuizletService;
import com.example.alexeyglushkov.quizletservice.tasks.QuizletServiceTaskProvider;
import com.example.alexeyglushkov.taskmanager.task.SimpleTask;
import com.example.alexeyglushkov.taskmanager.task.SimpleTaskManager;
import com.example.alexeyglushkov.taskmanager.task.Task;
import com.example.alexeyglushkov.taskmanager.task.TaskManager;

import junit.framework.Assert;

import java.io.File;
import java.util.UUID;

import com.example.alexeyglushkov.wordteacher.authorization.AuthActivityProxy;
import com.example.alexeyglushkov.wordteacher.model.CourseHolder;
import com.example.alexeyglushkov.wordteacher.model.CourseMerger;
import com.example.alexeyglushkov.wordteacher.model.CourseCodec;
import com.example.alexeyglushkov.cachemanager.preference.PreferenceStorage;

public class MainApplication extends Application {
    private @NonNull AccountStore accountStore;
    private @NonNull OAuthWebClient authWebClient;

    private @NonNull QuizletService quizletService;
    private @NonNull DropboxService dropboxService;
    private @NonNull CourseHolder courseHolder;
    private @NonNull TaskManager taskManager;

    private @NonNull
    Storage storage;

    public static @NonNull MainApplication instance;

    //// Initialization

    public MainApplication() {
        super();
        instance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        authWebClient = new AuthActivityProxy();
        taskManager = new SimpleTaskManager(10);

        File cacheDir = getDir("ServiceCache", MODE_PRIVATE);
        storage = new DiskStorage(cacheDir);

        cleanCache();
        loadAccountStore();

        createCourseHolder();
        loadCourseHolder();
    }

    //// Events

    private void onAccountStoreLoaded() {
        try {
            createQuizletService();
            createDropboxService();

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
        // TODO: make it async, but have to handle nil quizletService
        File authDir = getDir("AuthFolder", Context.MODE_PRIVATE);
        this.accountStore = new AccountCacheStore(authDir);
        this.accountStore.load();
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
        final Task cleanTask = new SimpleTask() {
            @Override
            public void startTask(Callback callback) {
                super.startTask(callback);

                StorageCleaner cleaner = new DiskStorageCleaner();
                cleaner.clean(getStorage());

                getPrivate().handleTaskCompletion(callback);
            }
        };

        taskManager.addTask(cleanTask);
    }

    private void merge(@NonNull File localFile, @NonNull DropboxAPI.Entry dropboxEntry, DropboxCommandProvider.MergeCompletion completion) {
        File outFile;

        try {
            File tmpDir = getCacheDir();

            UUID outFileName = UUID.randomUUID();
            outFile = File.createTempFile(outFileName.toString(), "", getCacheDir());

            FileMerger fileMerger = new FileObjectMerger(new CourseCodec(), new CourseMerger(), outFile);
            DropboxFileMerger merger = new DropboxFileMerger(dropboxService.getApi(), tmpDir, fileMerger);

            merger.merge(localFile, dropboxEntry, completion);

        } catch (Exception ex) {
            completion.completed(null, new Error("Merge exception", ex));
        }
    }

    //// Creation Methods

    private void createCourseHolder() {
        File authDir = getDir("CourseHolder", Context.MODE_PRIVATE);
        this.courseHolder = new CourseHolder(authDir);
    }

    private void createQuizletService() throws Exception {
        Account quizletAccount = Networks.getAccount(Networks.Network.Quizlet);
        QuizletServiceTaskProvider quizletCommandProvider = new QuizletServiceTaskProvider(getStorage());

        String id = Integer.toString(quizletAccount.getServiceType());
        ServiceCommandRunner serviceCommandRunner = new ServiceTaskRunner(getTaskManager(), id);

        this.quizletService = new QuizletService(quizletAccount, quizletCommandProvider, serviceCommandRunner);
        this.quizletService.restoreOrLoad(null);
    }

    private void createDropboxService() throws Exception {
        DropboxAccount dropboxAccount = (DropboxAccount)Networks.getAccount(Networks.Network.Dropbox);
        DropboxCommandProvider commandProvider = new DropboxServiceTaskProvider();

        String id = Integer.toString(dropboxAccount.getServiceType());
        ServiceCommandRunner serviceCommandRunner = new ServiceTaskRunner(getTaskManager(), id);

        Storage storage = new PreferenceStorage("DropboxServicePref", getContextProvider());

        dropboxService = new DropboxService(dropboxAccount, commandProvider, serviceCommandRunner, storage);
        dropboxService.setCallback(new DropboxService.Callback() {
            @Override
            public void merge(@NonNull File localFile, @NonNull DropboxAPI.Entry dropboxEntry, DropboxCommandProvider.MergeCompletion completion) {
                MainApplication.this.merge(localFile, dropboxEntry, completion);
            }
        });
    }

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

    public @NonNull QuizletService getQuizletService() {
        return quizletService;
    }

    public @NonNull DropboxService getDropboxService() {
        return dropboxService;
    }

    // Cast Getters

    public Context getCurrentContext() {
        return getCurrentActivity();
    }

    private Activity getCurrentActivity() {
        return AuthActivityProxy.getCurrentActivity();
    }
}
