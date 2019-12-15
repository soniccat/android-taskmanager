package com.main

import android.app.Application
import android.content.Context
import android.util.Log
import com.example.alexeyglushkov.authcachemanager.AccountCacheStore
import com.example.alexeyglushkov.authorization.Auth.AccountStore
import com.example.alexeyglushkov.authorization.Auth.ServiceCommandRunner
import com.example.alexeyglushkov.authorization.AuthActivityProxy
import com.example.alexeyglushkov.authorization.OAuth.OAuthWebClient
import com.example.alexeyglushkov.authtaskmanager.ServiceTaskRunner
import com.example.alexeyglushkov.cachemanager.SimpleStorageCleaner
import com.example.alexeyglushkov.cachemanager.Storage
import com.example.alexeyglushkov.cachemanager.StorageCleaner
import com.example.alexeyglushkov.cachemanager.disk.DiskStorage
import com.example.alexeyglushkov.quizletservice.QuizletService
import com.example.alexeyglushkov.quizletservice.tasks.QuizletServiceTaskProvider
import com.example.alexeyglushkov.taskmanager.task.*
import com.example.alexeyglushkov.taskmanager.task.coordinators.LimitTaskManagerCoordinator
import com.main.Networks.Network
import com.rssclient.model.RssStorage

class MainApplication : Application() {
    val TAG = "MainApplication"

    lateinit var accountStore: AccountStore
    lateinit var authWebClient: OAuthWebClient
    lateinit var quizletService: QuizletService

    lateinit private var coordinator: TaskManagerCoordinator
    lateinit var taskManager: TaskManager
        private set

    lateinit var rssStorage: RssStorage

    var storage: Storage? = null
        private set

    override fun onCreate() {
        super.onCreate()
        authWebClient = AuthActivityProxy()

        val newCoordinator = LimitTaskManagerCoordinator(10)
        newCoordinator.setLimit(1, 0.5f)
        newCoordinator.setLimit(2, 0.5f)
        coordinator = newCoordinator
        taskManager = SimpleTaskManager(coordinator)

        rssStorage = RssStorage("RssStorage")

        val cacheDir = getDir("ServiceCache", Context.MODE_PRIVATE)
        storage = DiskStorage(cacheDir)

        cleanCache()

        val authDir = this.getDir("AuthFolder", Context.MODE_PRIVATE)
        accountStore = AccountCacheStore(authDir)
        loadAccountStore()
    }

    fun cleanCache() {
        val safeStorage = storage ?: return
        val cleanTask: Task = object : SimpleTask() {
            override suspend fun startTask() {
                val cleaner: StorageCleaner = SimpleStorageCleaner()
                cleaner.clean(safeStorage)
                private.handleTaskCompletion()
            }
        }
        taskManager.addTask(cleanTask)
    }

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
            acc.setAuthCredentialStore(accountStore)
            Networks.restoreAuthorizer(acc)
        }
    }

    private fun onAccountStoreLoaded() {
        createQuizletService()
    }

    private fun createQuizletService() {
        val quizletAccount = Networks.getAccount(Network.Quizlet.ordinal)
        val quizletCommandProvider = QuizletServiceTaskProvider()
        val id = Integer.toString(quizletAccount.serviceType)
        val serviceCommandRunner: ServiceCommandRunner = ServiceTaskRunner(taskManager, id)
        quizletService = QuizletService(quizletAccount, quizletCommandProvider, serviceCommandRunner)
    }

    companion object {
        lateinit var instance: MainApplication
    }

    init {
        instance = this
    }
}