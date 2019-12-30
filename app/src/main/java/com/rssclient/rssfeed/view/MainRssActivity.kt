package com.rssclient.rssfeed.view

import android.app.AlertDialog.Builder
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aglushkov.taskmanager_http.image.Image
import com.aglushkov.taskmanager_http.image.ImageBinder
import com.main.MainApplication
import com.rssclient.controllers.ObjectCompletion
import com.rssclient.controllers.R
import com.rssclient.controllers.RssItemsActivity
import com.rssclient.model.RssFeed
import com.rssclient.rssfeed.vm.MainRssViewModel
import com.rssclient.rssfeed.vm.MainRssViewModelContract
import com.rssclient.vm.RssItemView
import com.rssclient.vm.showErrorDialog
import java.lang.Exception
import java.lang.NullPointerException
import java.net.URL

class MainRssActivity : AppCompatActivity() {
    private lateinit var vm: MainRssViewModel
    internal var listView: RecyclerView? = null

    // Events

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

    override fun onDestroy() {
        super.onDestroy()
        this.listView = null
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
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
                    activity.addRssFeed(url)
                }
            })
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    // Actions

    private fun bindView() {
        val listView = findViewById<View>(R.id.list) as RecyclerView
        this.listView = listView

        listView.layoutManager = LinearLayoutManager(listView.context, RecyclerView.VERTICAL, false)
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

        vm.eventLiveData.observe(this, Observer { event ->
            event?.take()?.let { _ ->
                handleEvent(event)
            }
        })

        vm.errorLiveData.observe(this, Observer { error ->
            error?.take()?.let { _ ->
                showErrorDialog(error)
            }
        })
    }

    internal fun createAdapter(data: List<RssItemView<*>>?) {
        val safeListView = listView
        if (safeListView == null) throw NullPointerException("ListView is null")

        val imageBinder = ImageBinder(object : ImageBinder.ImageLoader {
            override fun loadImage(image: Image, params: Map<String, Any>?, completion: (bitmap: Bitmap?, error: Exception?) -> Unit) {
                this@MainRssActivity.vm.onLoadImageRequested(image, completion)
            }
        })

        val feedBinder = RssFeedBinder(imageBinder).apply {
            listener = object : RssFeedBinder.Listener {
                override fun onClick(feed: RssFeed) {
                    this@MainRssActivity.vm.onRssFeedPressed(feed)
                }

                override fun onLongPressed(feed: RssFeed) {
                    this@MainRssActivity.vm.onRssFeedLongPressed(feed)
                }
            }
        }

        val adapter = RssFeedsAdapter(feedBinder)
        adapter.submitList(data)

        safeListView.adapter = adapter
    }

    private fun handleEvent(event: MainRssViewModelContract.Event) {
        when (event) {
            is MainRssViewModelContract.Event.ShowActionMode -> {
                this.startActionMode(event.feed)
            }
            is MainRssViewModelContract.Event.OpenRssFeed -> {
                openRssFeed(event.feed)
            }
        }
    }

    private fun openRssFeed(feed: RssFeed) {
        val intent = Intent(this, RssItemsActivity::class.java)
        intent.putExtra(RssItemsActivity.FEED_URL, feed.url.toString())
        startActivity(intent)
    }

    private fun addRssFeed(url: URL) {
        vm.onAddRssFeedPressed(url)
    }

    private fun deleteFeed(feed: RssFeed) {
        vm.onRssFeedDelete(feed)
    }

    private fun startActionMode(feed: RssFeed) {
        startSupportActionMode(object : ActionMode.Callback {
            override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
                return false
            }

            override fun onDestroyActionMode(mode: ActionMode) {}
            override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
                val inflater = mode.menuInflater
                inflater.inflate(R.menu.action_menu, menu)
                return true
            }

            override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
                if (item.itemId == R.id.action_delete_feed) {
                    deleteFeed(feed)
                    mode.finish()
                    return true
                }
                return false
            }
        })
    }

    private fun showAlertDialog(completion: ObjectCompletion<String>) {
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
        builder.create().show()
    }
}