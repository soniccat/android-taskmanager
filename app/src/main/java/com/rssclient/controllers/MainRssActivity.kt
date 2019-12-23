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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import com.aglushkov.repository.command.CancellableRepositoryCommand
import com.example.alexeyglushkov.taskmanager.task.TaskManager
import com.main.MainApplication
import com.rssclient.controllers.FeedsAdapter.FeedsAdapterListener
import com.rssclient.model.RssFeedRepository
import kotlinx.coroutines.*
import java.lang.Exception
import java.net.MalformedURLException
import java.net.URL

@SuppressLint("NewApi")
class MainRssActivity : AppCompatActivity(), FeedsAdapterListener {
    private lateinit var vm: MainRssViewModel
    internal var listView: ListView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        vm = ViewModelProviders.of(this).get(MainRssViewModel::class.java)
        observeViewModel()

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
                    lifecycleScope.launch {
                        whenStarted {
                            if (item.itemId == R.id.action_delete_feed) {
                                activity.deleteItemAtPos(position)
                                mode.finish()
                            }
                        }
                    }
                    return false
                }
            })
            true
        }
    }

    private fun observeViewModel() {
        vm.feedLiveData.observe(this, Observer {
            it?.let {

            }
        })
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
        val feed = rssRepository.getFeedsLiveData().value?.data()?.get(pos) ?: return

        // Do something in response to button
        val intent = Intent(this, RssItemsActivity::class.java)
        intent.putExtra(RssItemsActivity.FEED_URL, feed.url.toString())
        startActivity(intent)
    }

    internal fun updateTableAdapter() {
        val listview = findViewById<View>(R.id.listview) as ListView
        val feeds = rssRepository.getFeedsLiveData().value?.data() ?: emptyList()
        val adapter = FeedsAdapter(this, ArrayList(feeds), taskManager)
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
                    lifecycleScope.launch {
                        try {
                            val url = URL(result)
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

    private suspend fun addRssFeed(url: URL) {
        val activity = this
        try {
            val command = rssRepository.loadRssFeed(url, null)
            command as CancellableRepositoryCommand<*, *>
            command.join()

            val feed = command.liveData.value!!.data()!!

            rssRepository.addFeed(feed)
            activity.saveRssStorage()
            val adapter = activity.listView!!.adapter as FeedsAdapter
            adapter.add(feed)

        } catch (ex: Exception) {
            if (ex !is CancellationException) {
                Tools.showErrorMessage(activity, "Can't load rss")
            }
        }
    }

    private suspend fun deleteItemAtPos(pos: Int) {
        val feed = rssRepository.getFeedsLiveData().value?.data()?.get(pos) ?: return
        rssRepository.removeFeed(feed)
        saveRssStorage()

        val adapter = listView!!.adapter as FeedsAdapter
        adapter.remove(feed)
    }

    internal fun handleFeedKeeped() {}

    internal fun handleRssStorageLoad() {
        updateTableAdapter()
    }

    private suspend fun loadRssStorage() {
        val activity = this
        try {
            rssRepository.load()

            val feedsCount = rssRepository.getFeedsLiveData().value?.data()?.size ?: 0
            System.out.printf("loaded %d feeds\n", feedsCount)
            activity.handleRssStorageLoad()
        } catch (ex: Exception) {
            if (ex !is CancellationException) {
                Tools.showErrorMessage(activity, "RssStore Load Error")
            }
        }
    }

    private suspend fun saveRssStorage() {
        val activity = this
        try {
            rssRepository.save()

            val feedsCount = rssRepository.getFeedsLiveData().value?.data()?.size ?: 0
            System.out.printf("saved %d feeds\n", feedsCount)
            activity.handleFeedKeeped()
        } catch (ex: Exception) {
            if (ex !is CancellationException) {
                Tools.showErrorMessage(activity, "RssStore Save Error")
            }
        }
    }
}