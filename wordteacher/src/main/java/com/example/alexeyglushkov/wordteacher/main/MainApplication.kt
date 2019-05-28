package com.example.alexeyglushkov.wordteacher.main

//import com.dropbox.client2.DropboxAPI;

import android.app.Activity
import android.app.Application
import android.content.Context
import android.util.Log
import com.example.alexeyglushkov.authcachemanager.AccountCacheStore
import com.example.alexeyglushkov.authorization.Auth.Account
import com.example.alexeyglushkov.authorization.Auth.AccountStore
import com.example.alexeyglushkov.authorization.Auth.Authorizer
import com.example.alexeyglushkov.authorization.OAuth.OAuthWebClient
import com.example.alexeyglushkov.cachemanager.SimpleStorageCleaner
import com.example.alexeyglushkov.cachemanager.Storage
import com.example.alexeyglushkov.quizletservice.QuizletRepository
import com.example.alexeyglushkov.taskmanager.task.TaskManager
import com.example.alexeyglushkov.tools.ContextProvider
import com.example.alexeyglushkov.wordteacher.authorization.AuthActivityProxy
import com.example.alexeyglushkov.wordteacher.model.CourseHolder
import io.reactivex.internal.functions.Functions
import io.reactivex.schedulers.Schedulers
import org.junit.Assert
import javax.inject.Inject
import javax.inject.Named

class MainApplication : Application() {
    @Inject lateinit var accountStore: AccountStore
    @Inject lateinit var authWebClient: OAuthWebClient

    lateinit var quizletRepository: QuizletRepository
    //private @NonNull DropboxService dropboxService;
    lateinit var courseHolder: CourseHolder

    @Inject lateinit var taskManager: TaskManager
    @Inject lateinit var storage: Storage
    lateinit var component: MainComponent

    //    public @NonNull DropboxService getDropboxService() {
    //        return dropboxService;
    //    }

    // Cast Getters

    val currentContext: Context?
        get() = currentActivity

    private val currentActivity: Activity?
        get() = AuthActivityProxy.getCurrentActivity()

    //// Initialization

    @MainScope
    @dagger.Component(modules = [MainApplicationModule::class])
    interface MainComponent {
        val storage: Storage
        val taskManager: TaskManager
        val quizletRepozitory: QuizletRepository
        @get:Named("quizlet")
        val quizletAuthorizer: Authorizer
        @get:Named("quizlet")
        val quizletAccount: Account
        @get:Named("foursquare")
        val foursquareAuthorizer: Authorizer
        @get:Named("foursquare")
        val foursquareAccount: Account

        fun inject(app: MainApplication)
    }

    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()

        component = DaggerMainApplication_MainComponent.builder()
                .contextModule(ContextModule(applicationContext))
                .build()
        component.inject(this)

        cleanCache()
        loadAccountStore()

        createCourseHolder()
        loadCourseHolder()
    }

    //// Events

    private fun onAccountStoreLoaded() {
        quizletRepository = component.quizletRepozitory
        quizletRepository.restoreOrLoad(null)
        //createDropboxService();
    }

    //// Actions

    fun loadAccountStore() {
        try {
            this.accountStore.load()
        } catch (e: Exception) {
            Log.e(TAG, "Can't load account store")
            e.printStackTrace()
        }

        restoreAccounts(this.accountStore as AccountCacheStore)
        onAccountStoreLoaded()
    }

    private fun restoreAccounts(store: AccountCacheStore) {
        for (acc in store.accounts) {
            acc.setAuthCredentialStore(store)
            Networks.restoreAuthorizer(acc)
        }
    }

    fun loadCourseHolder() {
        taskManager.addTask(courseHolder.loadCourseListTask)
    }

    fun cleanCache() {
        val cleaner = SimpleStorageCleaner()
        cleaner.clean(storage)
                .subscribeOn(Schedulers.io())
                .subscribe(Functions.EMPTY_ACTION, Functions.emptyConsumer<Any>())
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

    private fun createCourseHolder() {
        val authDir = getDir("CourseHolder", Context.MODE_PRIVATE)
        this.courseHolder = CourseHolder(authDir)
    }

    companion object {
        private val TAG = "MainApplication"

        lateinit var instance: MainApplication
            private set

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

        val contextProvider: ContextProvider
            get() = ContextProvider { MainApplication.instance.currentContext }
    }
}
