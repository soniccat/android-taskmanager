package com.rssclient.controllers

import android.app.AlertDialog.Builder
import android.app.Dialog
import android.graphics.Bitmap
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aglushkov.taskmanager_http.image.Image
import com.main.MainApplication
import com.rssclient.vm.MainRssViewModel
import com.rssclient.vm.RssItemView
import com.rssclient.vm.showErrorDialog
import kotlinx.coroutines.*
import java.lang.Exception
import java.lang.NullPointerException
import java.net.MalformedURLException
import java.net.URL

class MainRssActivity : AppCompatActivity() {
    private lateinit var vm: MainRssViewModel
    internal var listView: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        vm = ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return MainRssViewModel(MainApplication.instance) as T
            }
        }).get(MainRssViewModel::class.java)
        observeViewModel()

        setContentView(R.layout.activity_main_rss)
        bindView()
    }

    private fun bindView() {
        val listView = findViewById<View>(R.id.list) as RecyclerView
        this.listView = listView

        listView.layoutManager = LinearLayoutManager(listView.context, RecyclerView.VERTICAL, false)

//        listView.onItemClickListener = OnItemClickListener { parent, view, position, id -> showFragmentActivityAtPos(position) }
//
//        listView.onItemLongClickListener = OnItemLongClickListener { parent, view, position, id ->
//            startActionMode(object : ActionMode.Callback {
//                override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
//                    return false
//                }
//
//                override fun onDestroyActionMode(mode: ActionMode) {}
//                override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
//                    val inflater = mode.menuInflater
//                    inflater.inflate(R.menu.action_menu, menu)
//                    return true
//                }
//
//                override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
//                    lifecycleScope.launch {
//                        whenStarted {
//                            if (item.itemId == R.id.action_delete_feed) {
//                                deleteItemAtPos(position)
//                                mode.finish()
//                            }
//                        }
//                    }
//                    return false
//                }
//            })
//            true
//        }
    }

    override fun onDestroy() {
        super.onDestroy()
        this.listView = null
    }

    private fun observeViewModel() {
        vm.feedLiveData.observe(this, Observer {
            it?.let {
                val data = it.data() ?: emptyList()
                val adapter = listView?.adapter as? RssFeedsAdapter
                if (adapter == null) {
                    createAdapter(data)
                } else {
                    adapter.submitList(data)
                }
            }
        })

        vm.errorLiveData.observe(this, Observer { error ->
            if (error == null || error.isHandled) return@Observer
            error.take()?.let { event ->
                showErrorDialog(error)
            }
        })
    }

    fun showFragmentActivityAtPos(pos: Int) {
        // TODO:
//        val feed = rssRepository.getFeedsLiveData().value?.data()?.get(pos) ?: return
//
//        val intent = Intent(this, RssItemsActivity::class.java)
//        intent.putExtra(RssItemsActivity.FEED_URL, feed.url.toString())
//        startActivity(intent)
    }

    internal fun createAdapter(data: List<RssItemView<*>>?) {
        val safeListView = listView
        if (safeListView == null) throw NullPointerException("ListView is null")

        var adapter = safeListView.adapter
        if (adapter == null) {
            val imageBinder = ImageBinder(object : ImageBinder.ImageLoader {
                override fun loadImage(image: Image, params: Map<String, Any>?, completion: (bitmap: Bitmap?, error: Exception?) -> Unit) {
                    this@MainRssActivity.vm.onLoadImageRequested(image, completion)
                }
            })
            adapter = RssFeedsAdapter(imageBinder)
            adapter.submitList(data)
            safeListView.adapter = adapter
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean { // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_settings) {
            return true
        } else if (id == R.id.add_feed) {
            val activity = this
            showAlertDialog(object : ObjectCompletion<String> {
                override fun completed(result: String) {
                    val url = URL(result)
                    lifecycleScope.launch {
                        try {
                            activity.addRssFeed(url)
                        } catch (e: MalformedURLException) { // TODO Auto-generated catch block
                            e.printStackTrace()
                        }
                    }
                }
            })
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    internal fun showAlertDialog(completion: ObjectCompletion<String>) {
        val builder = Builder(this)
        builder.setMessage("Type a feed url")
        val textView = EditText(this)
        textView.setText("https://www.lenta.ru/rss")
        builder.setView(textView)
        builder.setPositiveButton("Ok") { dialog, which ->
            println("Ok pressed")
            val string = textView.text.toString()
            completion.completed(string)
        }
        builder.setNegativeButton("Cancel") { dialog, which -> println("Cancel pressed") }
        val dialog: Dialog = builder.create()
        dialog.show()
    }

    private fun addRssFeed(url: URL) {
        vm.onAddRssFeedPressed(url)
    }

    private suspend fun deleteItemAtPos(pos: Int) {
//        val feed = rssRepository.getFeedsLiveData().value?.data()?.get(pos) ?: return
//        rssRepository.removeFeed(feed)
//        saveRssStorage()
//
//        val adapter = listView!!.adapter as FeedsAdapter
//        adapter.remove(feed)
    }

//    internal fun handleFeedKeeped() {}

//    internal fun handleRssStorageLoad() {
//        updateTableAdapter()
//    }

//    private suspend fun loadRssStorage() {
//        val activity = this
//        try {
//            rssRepository.load()
//
//            val feedsCount = rssRepository.getFeedsLiveData().value?.data()?.size ?: 0
//            System.out.printf("loaded %d feeds\n", feedsCount)
//            activity.handleRssStorageLoad()
//        } catch (ex: Exception) {
//            if (ex !is CancellationException) {
//                Tools.showErrorMessage(activity, "RssStore Load Error")
//            }
//        }
//    }

//    private suspend fun saveRssStorage() {
//        val activity = this
//        try {
//            rssRepository.save()
//
//            val feedsCount = rssRepository.getFeedsLiveData().value?.data()?.size ?: 0
//            System.out.printf("saved %d feeds\n", feedsCount)
//            activity.handleFeedKeeped()
//        } catch (ex: Exception) {
//            if (ex !is CancellationException) {
//                Tools.showErrorMessage(activity, "RssStore Save Error")
//            }
//        }
//    }
}