package com.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
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
import com.example.alexeyglushkov.taskmanager.task.Task
import com.example.alexeyglushkov.taskmanager.task.TaskImpl
import com.main.Networks.Network
import com.rssclient.controllers.R
import com.rssclient.controllers.databinding.FragmentLauncherBinding
import com.rssclient.rssfeed.view.RssMainFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class LauncherFragment : Fragment() {
    private val scope = MainScope()
    private var binding: FragmentLauncherBinding? = null

    private var service: SimpleService? = null
    private var storage: Storage? = null

    private val mainApplication = MainApplication.instance
    val taskManager = mainApplication.taskManager
    val accountStore = mainApplication.accountStore

    var listener: Listener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentLauncherBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding!!.toolbar.apply {
            inflateMenu(R.menu.menu_main)
            setOnMenuItemClickListener {
                onOptionsItemSelected(it)
            }
        }

        val listView = binding!!.list
        listView.adapter = ArrayAdapter(view.context, android.R.layout.activity_list_item, android.R.id.text1, arrayOf("Rss Client", "Authorization", "Run Request", "Clear cache", "Load Sets"))
        listView.onItemClickListener = OnItemClickListener { _, _, position, _ ->
            if (position == 0) {
                showRssClient()
            } else if (position == 1) {
                showAuthorization()
            } else if (position == 2) {
                scope.launch { requestUser() }
            } else if (position == 3) {
                clearCache()
            } else if (position == 4) {
                scope.launch { loadSets(true) }
            }
        }
    }

    private fun showRssClient() {
        listener?.onOpenRssPressed()
    }

    private fun showAuthorization() {
        val authTask: Task = object : TaskImpl() {
            override suspend fun startTask() {
                val account = Networks.createAccount(Network.Quizlet)
                val creds = withContext(Dispatchers.IO) {
                    account.authorize()
                }

                Log.d(TAG, "showAuthorization onFinished " + creds.isValid)
            }
        }
        taskManager.addTask(authTask)
    }

    private suspend fun requestUser() {
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
        val serviceAccount = if (accounts.size > 0) {
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
        get() {
            val cacheDir = context!!.getDir("ServiceCache", Context.MODE_PRIVATE)
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

    private suspend fun loadSets(useCache: Boolean) {
        mainApplication.quizletService.loadSets(null)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        return if (id == R.id.action_settings) {
            true
        } else super.onOptionsItemSelected(item)
    }

    companion object {
        private const val TAG = "LauncherFragment"
    }

    interface Listener {
        fun onOpenRssPressed()
    }
}