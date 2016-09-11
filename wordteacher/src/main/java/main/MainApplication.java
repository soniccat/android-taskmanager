package main;

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
import com.example.alexeyglushkov.cachemanager.StorageProvider;
import com.example.alexeyglushkov.cachemanager.disk.DiskStorageCleaner;
import com.example.alexeyglushkov.cachemanager.disk.DiskStorageProvider;
import com.example.alexeyglushkov.dropboxservice.ContextProvider;
import com.example.alexeyglushkov.dropboxservice.DropboxAccount;
import com.example.alexeyglushkov.dropboxservice.DropboxCommandProvider;
import com.example.alexeyglushkov.dropboxservice.DropboxFileMerger;
import com.example.alexeyglushkov.dropboxservice.DropboxHelper;
import com.example.alexeyglushkov.dropboxservice.DropboxService;
import com.example.alexeyglushkov.dropboxservice.DropboxServiceTaskProvider;
import com.example.alexeyglushkov.dropboxservice.DropboxSyncCommand;
import com.example.alexeyglushkov.dropboxservice.FileMerger;
import com.example.alexeyglushkov.dropboxservice.FileObjectMerger;
import com.example.alexeyglushkov.quizletservice.QuizletService;
import com.example.alexeyglushkov.quizletservice.tasks.QuizletServiceTaskProvider;
import com.example.alexeyglushkov.taskmanager.task.SimpleTask;
import com.example.alexeyglushkov.taskmanager.task.SimpleTaskManager;
import com.example.alexeyglushkov.taskmanager.task.Task;
import com.example.alexeyglushkov.taskmanager.task.TaskManager;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import authorization.AuthActivityProxy;
import model.CourseHolder;
import model.CourseMerger;
import model.CourseSerializer;
import preferencestorage.PreferenceStorageProvider;

public class MainApplication extends Application {
    private AccountStore accountStore;
    private OAuthWebClient authWebClient;

    private QuizletService quizletService;
    private DropboxService dropboxService;
    private CourseHolder courseHolder;
    private TaskManager taskManager;

    private StorageProvider storageProvider;

    public static MainApplication instance;

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
        storageProvider = new DiskStorageProvider(cacheDir);

        cleanCache();
        loadAccountStore();

        createCourseHolder();
        loadCourseHolder();
    }

    //// Events

    private void onAccountStoreLoaded() {
        createQuizletService();
        createDropboxService();
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
            public void startTask() {
                StorageCleaner cleaner = new DiskStorageCleaner();
                cleaner.clean(getStorageProvider());

                getPrivate().handleTaskCompletion();
            }
        };

        taskManager.addTask(cleanTask);
    }

    //// Creation Methods

    private void createCourseHolder() {
        File authDir = getDir("CourseHolder", Context.MODE_PRIVATE);
        this.courseHolder = new CourseHolder(authDir);
    }

    private void createQuizletService() {
        Account quizletAccount = Networks.getAccount(Networks.Network.Quizlet);
        QuizletServiceTaskProvider quizletCommandProvider = new QuizletServiceTaskProvider(getStorageProvider());

        String id = Integer.toString(quizletAccount.getServiceType());
        ServiceCommandRunner serviceCommandRunner = new ServiceTaskRunner(getTaskManager(), id);

        this.quizletService = new QuizletService(quizletAccount, quizletCommandProvider, serviceCommandRunner);
        this.quizletService.restore();
    }

    private void createDropboxService() {
        DropboxAccount dropboxAccount = (DropboxAccount)Networks.getAccount(Networks.Network.Dropbox);
        DropboxCommandProvider commandProvider = new DropboxServiceTaskProvider();

        String id = Integer.toString(dropboxAccount.getServiceType());
        ServiceCommandRunner serviceCommandRunner = new ServiceTaskRunner(getTaskManager(), id);

        StorageProvider storage = new PreferenceStorageProvider("DropboxServicePref", getContextProvider());

        dropboxService = new DropboxService(dropboxAccount, commandProvider, serviceCommandRunner, storage);
        dropboxService.setCallback(new DropboxService.Callback() {
            @Override
            public void merge(@NonNull File localFile, @NonNull DropboxAPI.Entry dropboxEntry, DropboxCommandProvider.MergeCompletion completion) {
                MainApplication.this.merge(localFile, dropboxEntry, completion);
            }
        });
    }

    private void merge(@NonNull File localFile, @NonNull DropboxAPI.Entry dropboxEntry, DropboxCommandProvider.MergeCompletion completion) {
        try {
            File tmpDir = getCacheDir();

            UUID outFileName = UUID.randomUUID();
            File outFile = File.createTempFile(outFileName.toString(), "", getCacheDir());

            FileMerger fileMerger = new FileObjectMerger(new CourseSerializer(), new CourseMerger(), outFile);
            DropboxFileMerger merger = new DropboxFileMerger(dropboxService.getApi(), tmpDir, fileMerger);

            merger.merge(localFile, dropboxEntry, completion);

        } catch (Exception ex) {
            completion.completed(null, new Error("Merge exception", ex));
        }
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

    public TaskManager getTaskManager() {
        return taskManager;
    }

    public AccountStore getAccountStore() {
        return accountStore;
    }

    public CourseHolder getCourseHolder() {
        return courseHolder;
    }

    public OAuthWebClient getAuthWebClient() {
        return authWebClient;
    }

    public StorageProvider getStorageProvider() {
        return storageProvider;
    }

    public QuizletService getQuizletService() {
        return quizletService;
    }

    public DropboxService getDropboxService() {
        return dropboxService;
    }

    // Cast Getters

    public Context getCurrentContext() {
        return ((AuthActivityProxy)authWebClient).getCurrentActivity();
    }

    //// Inner Interfaces

    public interface ReadyListener {
        void onReady();
    }
}
