package com.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.ListView
import com.example.alexeyglushkov.authorization.Auth.Account
import com.example.alexeyglushkov.authorization.Auth.AccountStore
import com.example.alexeyglushkov.authorization.requestbuilder.HttpUrlConnectionBuilder
import com.example.alexeyglushkov.authtaskmanager.HttpServiceCommand
import com.example.alexeyglushkov.authtaskmanager.ServiceTaskProvider
import com.example.alexeyglushkov.authtaskmanager.ServiceTaskRunner
import com.example.alexeyglushkov.cachemanager.Storage
import com.example.alexeyglushkov.cachemanager.clients.Cache
import com.example.alexeyglushkov.cachemanager.clients.SimpleCache
import com.example.alexeyglushkov.cachemanager.disk.DiskStorage
import com.example.alexeyglushkov.service.SimpleService
import com.example.alexeyglushkov.streamlib.convertors.BytesStringConverter
import com.example.alexeyglushkov.taskmanager.task.SimpleTask
import com.example.alexeyglushkov.taskmanager.task.Task
import com.example.alexeyglushkov.taskmanager.task.TaskManager
import com.main.Networks.Network
import com.rssclient.controllers.MainRssActivity
import com.rssclient.controllers.R
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers

class MainActivity : BaseActivity() {
    private var service: SimpleService? = null
    private var storage: Storage? = null
    private val mainApplication: MainApplication
        private get() = application as MainApplication

    val taskManager: TaskManager
        get() = mainApplication.taskManager

    val accountStore: AccountStore
        get() = mainApplication.accountStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val listView = findViewById<View>(R.id.list) as ListView
        listView.adapter = ArrayAdapter(this, android.R.layout.activity_list_item, android.R.id.text1, arrayOf("Rss Client", "Authorization", "Run Request", "Clear cache", "Load Sets"))
        listView.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            if (position == 0) {
                showRssClient()
            } else if (position == 1) {
                showAuthorization()
            } else if (position == 2) {
                requestUser()
            } else if (position == 3) {
                clearCache()
            } else if (position == 4) {
                loadSets(true)
            }
        }
    }

    private fun showRssClient() {
        val intent = Intent(this, MainRssActivity::class.java)
        startActivity(intent)
    }

    private fun showAuthorization() {
        val authTask: Task = object : SimpleTask() {
            override suspend fun startTask() {
                val account = Networks.createAccount(Network.Quizlet)
                account.authorize().subscribeOn(Schedulers.io()).subscribe(Consumer {
                    Log.d(TAG, "showAuthorization onFinished " + account.credentials.isValid)
                    private.handleTaskCompletion()
                })
            }
        }
        taskManager.addTask(authTask)
    }

    private fun requestUser() {
        if (service == null) {
            initService()
        }
        val builder = HttpUrlConnectionBuilder()
        builder.setUrl("https://api.foursquare.com/v2/users/self?v=20140806&m=foursquare")
        val cmd: HttpServiceCommand<*> = HttpServiceCommand<String>(builder, BytesStringConverter())
        val cache = SimpleCache(storage!!)
        cache.cacheMode = Cache.CacheMode.CHECK_CACHE_IF_ERROR_THEN_LOAD
        cmd.setCacheClient(cache)
        service!!.runCommand(cmd, true)
    }

    private fun initService() {
        service = SimpleService()
        val accounts = accountStore.getAccounts(Network.Quizlet.ordinal)
        var serviceAccount: Account? = null
        serviceAccount = if (accounts.size > 0) {
            accounts[0]
        } else {
            Networks.createAccount(Network.Quizlet)
        }
        service!!.account = serviceAccount
        // TODO: we need the solution for id
        service!!.setServiceCommandProvider(ServiceTaskProvider())
        service!!.setServiceCommandRunner(ServiceTaskRunner(taskManager, "31234"))
        storage = serviceCache
    }

    private val serviceCache: Storage
        private get() {
            val cacheDir = getDir("ServiceCache", Context.MODE_PRIVATE)
            return DiskStorage(cacheDir)
        }

    private fun clearCache() {
        try {
            accountStore.removeAll()
            serviceCache.removeAll()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadSets(useCache: Boolean) {
        mainApplication.quizletService.loadSets(null)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean { // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean { // Handle action bar item clicks here. The action bar will
// automatically handle clicks on the Home/Up button, so long
// as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        return if (id == R.id.action_settings) {
            true
        } else super.onOptionsItemSelected(item)
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}