package main;

import android.app.Application;
import android.content.Context;

import com.example.alexeyglushkov.authcachemanager.AccountCacheStore;
import com.example.alexeyglushkov.authorization.Auth.Account;
import com.example.alexeyglushkov.authorization.Auth.AccountStore;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommandRunner;
import com.example.alexeyglushkov.authorization.OAuth.OAuthWebClient;
import com.example.alexeyglushkov.authtaskmanager.ServiceTaskRunner;
import com.example.alexeyglushkov.cachemanager.CacheCleaner;
import com.example.alexeyglushkov.cachemanager.CacheProvider;
import com.example.alexeyglushkov.cachemanager.DiskCacheCleaner;
import com.example.alexeyglushkov.cachemanager.DiskCacheMetadata;
import com.example.alexeyglushkov.cachemanager.DiskCacheProvider;
import com.example.alexeyglushkov.quizletservice.QuizletService;
import com.example.alexeyglushkov.quizletservice.tasks.QuizletServiceTaskProvider;
import com.example.alexeyglushkov.taskmanager.task.SimpleTask;
import com.example.alexeyglushkov.taskmanager.task.SimpleTaskManager;
import com.example.alexeyglushkov.taskmanager.task.Task;
import com.example.alexeyglushkov.taskmanager.task.TaskManager;

import java.io.File;

import authorization.AuthActivityProxy;
import model.CourseHolder;

public class MainApplication extends Application {
    private AccountStore accountStore;
    private OAuthWebClient authWebClient;

    private QuizletService quizletService;
    private CourseHolder courseHolder;
    private TaskManager taskManager;

    private CacheProvider cacheProvider;

    public static MainApplication instance;

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
        cacheProvider = new DiskCacheProvider(cacheDir);

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

    public CacheProvider getCacheProvider() {
        return cacheProvider;
    }

    public QuizletService getQuizletService() {
        return quizletService;
    }

    public void cleanCache() {
        final Task cleanTask = new SimpleTask() {
            @Override
            public void startTask() {
                CacheCleaner cleaner = new DiskCacheCleaner();
                cleaner.clean(getCacheProvider());

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
            acc.setAuthCredentialStore(getAccountStore());
            Networks.restoreAuthorizer(acc);
        }
    }

    private void onAccountStoreLoaded() {
        createQuizletService();
    }

    private void createQuizletService() {
        Account quizletAccount = Networks.getAccount(Networks.Network.Quizlet);
        QuizletServiceTaskProvider quizletCommandProvider = new QuizletServiceTaskProvider(getCacheProvider());

        String id = Integer.toString(quizletAccount.getServiceType());
        ServiceCommandRunner serviceCommandRunner = new ServiceTaskRunner(getTaskManager(), id);

        quizletService = new QuizletService(quizletAccount, quizletCommandProvider, serviceCommandRunner);
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

    }
}
