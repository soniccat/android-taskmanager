package main;

import android.app.Application;
import android.content.Context;

import com.example.alexeyglushkov.authcachemanager.AccountCacheStore;
import com.example.alexeyglushkov.authorization.Auth.Account;
import com.example.alexeyglushkov.authorization.Auth.AccountStore;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommandRunner;
import com.example.alexeyglushkov.authorization.OAuth.OAuthWebClient;
import com.example.alexeyglushkov.authtaskmanager.ServiceTaskRunner;
import com.example.alexeyglushkov.cachemanager.StorageCleaner;
import com.example.alexeyglushkov.cachemanager.StorageProvider;
import com.example.alexeyglushkov.cachemanager.DiskStorageCleaner;
import com.example.alexeyglushkov.cachemanager.DiskStorageProvider;
import com.example.alexeyglushkov.dropboxservice.DropboxAccount;
import com.example.alexeyglushkov.dropboxservice.DropboxCommandProvider;
import com.example.alexeyglushkov.dropboxservice.DropboxService;
import com.example.alexeyglushkov.dropboxservice.DropboxServiceTaskProvider;
import com.example.alexeyglushkov.quizletservice.QuizletService;
import com.example.alexeyglushkov.quizletservice.tasks.QuizletServiceTaskProvider;
import com.example.alexeyglushkov.taskmanager.task.SimpleTask;
import com.example.alexeyglushkov.taskmanager.task.SimpleTaskManager;
import com.example.alexeyglushkov.taskmanager.task.Task;
import com.example.alexeyglushkov.taskmanager.task.TaskManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import authorization.AuthActivityProxy;
import model.CourseHolder;

public class MainApplication extends Application {
    private AccountStore accountStore;
    private OAuthWebClient authWebClient;

    private QuizletService quizletService;
    private DropboxService dropboxService;
    private CourseHolder courseHolder;
    private TaskManager taskManager;

    private StorageProvider storageProvider;

    public static MainApplication instance;

    private List<ReadyListener> courseHolderListeners = new ArrayList<>();
    private List<ReadyListener> quizletServiceListeners = new ArrayList<>();

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
        loadCourseHolder();
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

    public Context getCurrentContext() {
        return ((AuthActivityProxy)authWebClient).getCurrentActivity();
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

    public void loadAccountStore() {
        final Task loadAccountTask = new SimpleTask() {
            @Override
            public void startTask() {
                File authDir = getDir("AuthFolder", Context.MODE_PRIVATE);
                AccountCacheStore store = new AccountCacheStore(authDir);
                store.load();
                restoreAccounts(store);

                getPrivate().setTaskUserData(store);
                getPrivate().handleTaskCompletion();
            }
        };

        loadAccountTask.setTaskCallback(new Task.Callback() {
            @Override
            public void onCompleted(boolean cancelled) {
                MainApplication.this.accountStore = (AccountCacheStore) loadAccountTask.getTaskUserData();
                onAccountStoreLoaded();
            }
        });

        taskManager.addTask(loadAccountTask);
    }

    private void restoreAccounts(AccountCacheStore store) {
        for (Account acc : store.getAccounts()) {
            acc.setAuthCredentialStore(store);
            Networks.restoreAuthorizer(acc);
        }
    }

    private void onAccountStoreLoaded() {
        createQuizletService();
        createDropboxService();
    }

    private void createQuizletService() {
        Account quizletAccount = Networks.getAccount(Networks.Network.Quizlet);
        QuizletServiceTaskProvider quizletCommandProvider = new QuizletServiceTaskProvider(getStorageProvider());

        String id = Integer.toString(quizletAccount.getServiceType());
        ServiceCommandRunner serviceCommandRunner = new ServiceTaskRunner(getTaskManager(), id);

        quizletService = new QuizletService(quizletAccount, quizletCommandProvider, serviceCommandRunner);
        quizletService.restore();
    }

    private void createDropboxService() {
        DropboxAccount dropboxAccount = (DropboxAccount)Networks.getAccount(Networks.Network.Dropbox);
        DropboxCommandProvider commandProvider = new DropboxServiceTaskProvider();

        String id = Integer.toString(dropboxAccount.getServiceType());
        ServiceCommandRunner serviceCommandRunner = new ServiceTaskRunner(getTaskManager(), id);

        dropboxService = new DropboxService(dropboxAccount, commandProvider, serviceCommandRunner);
    }

    private void loadCourseHolder() {
        final Task loadCourseHolderTask = new SimpleTask() {
            @Override
            public void startTask() {
                File authDir = getDir("CourseHolder", Context.MODE_PRIVATE);
                CourseHolder store = new CourseHolder(authDir);
                store.loadCourses();

                getPrivate().setTaskUserData(store);
                getPrivate().handleTaskCompletion();
            }
        };

        loadCourseHolderTask.setTaskCallback(new Task.Callback() {
            @Override
            public void onCompleted(boolean cancelled) {
                MainApplication.this.courseHolder = (CourseHolder) loadCourseHolderTask.getTaskUserData();
                onCourseHolderLoaded();
            }
        });

        taskManager.addTask(loadCourseHolderTask);
    }

    private void onCourseHolderLoaded() {
        for (ReadyListener listener : courseHolderListeners) {
            listener.onReady();
        }

        courseHolderListeners.clear();
    }

    public void addHolderListener(ReadyListener listener) {
        if (courseHolder != null) {
            listener.onReady();
        } else {
            courseHolderListeners.add(listener);
        }
    }

    public interface ReadyListener {
        void onReady();
    }
}
