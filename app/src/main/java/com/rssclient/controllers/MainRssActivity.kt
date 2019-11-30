package com.rssclient.controllers

import android.annotation.SuppressLint
import android.app.AlertDialog.Builder
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AbsListView
import android.widget.AbsListView.OnScrollListener
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView.OnItemLongClickListener
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.alexeyglushkov.taskmanager.task.Task.Callback
import com.example.alexeyglushkov.taskmanager.task.TaskManager
import com.main.MainApplication
import com.rssclient.controllers.FeedsAdapter.FeedsAdapterListener
import com.rssclient.model.RssFeed
import com.rssclient.model.RssStorage
import java.net.MalformedURLException
import java.net.URL

@SuppressLint("NewApi")
class MainRssActivity : AppCompatActivity(), FeedsAdapterListener {
    lateinit var taskManager: TaskManager
    lateinit var rssStorage: RssStorage
    internal var listView: ListView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val application = application as MainApplication
        if (!this::taskManager.isInitialized) {
            taskManager = application.taskManager
        }

        if (!this::rssStorage.isInitialized) {
            rssStorage = application.rssStorage
            if (rssStorage.feeds.size == 0) { // TODO: update to livedata
                loadRssStorage()
            }
        }

        setContentView(R.layout.activity_main_rss)

        val listview = findViewById<View>(R.id.listview) as ListView
        listView = listview

        updateTableAdapter()



        val activity = this
        listview.onItemClickListener = OnItemClickListener { parent, view, position, id -> activity.showFragmentActivityAtPos(position) }
        listview.setOnScrollListener(object : OnScrollListener {
            override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {}
            override fun onScroll(view: AbsListView, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
                val adapter = listview.adapter as FeedsAdapter
                adapter.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount)
            }
        })

        listview.onItemLongClickListener = OnItemLongClickListener { parent, view, position, id ->
            activity.startActionMode(object : ActionMode.Callback {
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
                        activity.deleteItemAtPos(position)
                        mode.finish()
                    }
                    return false
                }
            })
            true
        }
    }

    override fun getViewAtPosition(pos: Int): View? {
        val firstListItemPosition = listView!!.firstVisiblePosition
        val lastListItemPosition = firstListItemPosition + listView!!.childCount - 1
        if (pos >= firstListItemPosition && pos <= lastListItemPosition) {
            val childIndex = pos - firstListItemPosition
            return listView!!.getChildAt(childIndex)
        }
        return null
    }

    fun showFragmentActivityAtPos(pos: Int) {
        val feed = rssStorage.feeds[pos]
        // Do something in response to button
        val intent = Intent(this, RssItemsActivity::class.java)
        intent.putExtra(RssItemsActivity.FEED_URL, feed.url.toString())
        startActivity(intent)
    }

    internal fun showItemDialogAtPos(pos: Int) {
        val builder = Builder(this)
        val items = arrayOf("Delete", "Cancel")
        val activity = this
        builder.setTitle("Choose an action")
        builder.setItems(items) { dialog, which ->
            if (which == 0) {
                activity.deleteItemAtPos(pos)
            }
        }
        builder.create().show()
    }

    internal fun loadFeedAtPos(pos: Int) {
        val feed = rssStorage.feeds[pos]
        rssStorage.loadFeed(taskManager, this, feed, object : RssStorage.RssFeedCallback {
            override fun completed(feed: RssFeed?, error: Error?) {
                println("feed loaded")
            }
        })
    }

    internal fun updateTableAdapter() {
        val listview = findViewById<View>(R.id.listview) as ListView
        val feeds = ArrayList(rssStorage.feeds)
        val adapter = FeedsAdapter(this, feeds, taskManager)
        adapter.listener = this
        listview.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean { // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean { // Handle action bar item clicks here. The action bar will
// automatically handle clicks on the Home/Up button, so long
// as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        if (id == R.id.action_settings) {
            return true
        } else if (id == R.id.add_feed) {
            val activity = this
            showAlertDialog(object : ObjectCompletion<String> {
                override fun completed(result: String) {
                    try {
                        val url = URL(result)
                        activity.addRssFeed(url)
                    } catch (e: MalformedURLException) { // TODO Auto-generated catch block
                        e.printStackTrace()
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

    internal fun addRssFeed(url: URL) {
        val feed = RssFeed(url, url.toString())
        val activity = this
        rssStorage.loadFeed(taskManager, this, feed, object : RssStorage.RssFeedCallback {
            override fun completed(feed: RssFeed?, error: Error?) {
                if (error != null) {
                    Tools.showErrorMessage(activity, "Can't load rss")
                } else if (feed != null) {
                    rssStorage.addFeed(feed)
                    activity.saveRssStorage()
                    val adapter = activity.listView!!.adapter as FeedsAdapter
                    adapter.add(feed)
                }
            }
        })
    }

    internal fun deleteItemAtPos(pos: Int) {
        val feed = rssStorage.feeds[pos]
        rssStorage.deleteFeed(feed)
        saveRssStorage()
        val adapter = listView!!.adapter as FeedsAdapter
        adapter.remove(feed)
    }

    internal fun handleFeedKeeped() {}
    internal fun handleRssStorageLoad() {
        updateTableAdapter()
    }

    internal fun loadRssStorage() {
        val activity = this
        rssStorage.load(taskManager, this, object : RssStorage.RssStorageCallback {
            override fun completed(storage: RssStorage?, error: Error?) {
                if (error != null) {
                    Tools.showErrorMessage(activity, "RssStore Load Error")
                } else if (storage != null) {
                    val feedsCount = storage.feeds.size
                    System.out.printf("loaded %d feeds\n", feedsCount)
                    activity.handleRssStorageLoad()
                }
            }
        })
    }

    internal fun saveRssStorage() {
        val activity = this
        rssStorage.keep(taskManager, this, object : RssStorage.RssStorageCallback {
            override fun completed(storage: RssStorage?, error: Error?) {
                if (error != null) {
                    Tools.showErrorMessage(activity, "RssStore Save Error")
                } else if (storage != null) {
                    val feedsCount = storage.feeds.size
                    System.out.printf("saved %d feeds\n", feedsCount)
                    activity.handleFeedKeeped()
                }
            }
        })
    }
}