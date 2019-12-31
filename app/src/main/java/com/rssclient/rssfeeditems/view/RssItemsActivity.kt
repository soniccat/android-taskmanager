package com.rssclient.rssfeeditems.view

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AbsListView
import android.widget.AbsListView.OnScrollListener
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.aglushkov.taskmanager_http.image.Image
import com.aglushkov.taskmanager_http.image.ImageLoader
import com.example.alexeyglushkov.streamlib.progress.ProgressInfo
import com.example.alexeyglushkov.streamlib.progress.ProgressListener
import com.example.alexeyglushkov.taskmanager.task.*
import com.example.alexeyglushkov.taskmanager.task.PriorityTaskProvider.PriorityProvider
import com.example.alexeyglushkov.taskmanager.ui.TaskManagerView
import com.example.alexeyglushkov.tools.Range
import com.main.MainApplication
import com.rssclient.controllers.R
import com.rssclient.rssfeeditems.view.RssItemsAdapter.RssItemsAdapterListener
import com.rssclient.rssfeeditems.view.RssItemsAdapter.ViewHolder
import com.rssclient.model.RssFeed
import com.rssclient.model.RssFeedRepository
import com.rssclient.model.RssItem
import com.rssclient.rssfeed.vm.MainRssViewModel
import com.rssclient.rssfeeditems.vm.RssItemsViewModel
import com.rssclient.rssfeeditems.vm.RssItemsViewModelContract
import org.junit.Assert
import java.net.MalformedURLException
import java.net.URL

class RssItemsActivity : AppCompatActivity(), RssItemsAdapterListener, ProgressListener {
    private lateinit var vm: RssItemsViewModelContract
    private var listView: ListView? = null
    private var taskManagerView: TaskManagerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        vm = ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return RssItemsViewModel(MainApplication.instance, intent.extras) as T
            }
        }).get(RssItemsViewModel::class.java)
        observeViewModel()

        setContentView(R.layout.activity_rss_items)
        bindView()

        val activity = this
        if (feed.items.size == 0) {
//            rssRepository.loadRssFeed(feed.url, null)
//            rssRepository.loa(taskManager, this, feed, object : RssFeedCallback {
//                override fun completed(feed: RssFeed?, error: Error?) {
//                    println("loaded")
//                    HandlerTools.runOnMainThread {
//                        if (error != null) {
//                            Tools.showErrorMessage(activity, "Rss Load Error")
//                        } else {
//                            activity.updateTableAdapter()
//                        }
//                    }
//                }
//            })
        } else {
            activity.updateTableAdapter()
        }
    }

    private fun bindView() {
        taskManagerView = findViewById<View>(R.id.task_manager_view) as TaskManagerView
        listView = findViewById<View>(R.id.listview) as ListView

        listView?.setOnScrollListener(object : OnScrollListener {
            override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {}
            override fun onScroll(view: AbsListView, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
                val adapter = view.adapter as? RssItemsAdapter
                if (adapter != null) {
                    this@RssItemsActivity.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount)
                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        listView = null
        taskManagerView = null
    }

    //// Actions

    private fun observeViewModel() {
        vm.rssItems.observe(this, Observer {
            if (it == null) return@Observer

        })

        vm.taskManagerSnapshot.observe(this, Observer {
            if (it == null) return@Observer
            taskManagerView?.showSnapshot(it)
        })
    }

    fun getViewAtPosition(pos: Int): View? {
        val firstListItemPosition = listView.firstVisiblePosition
        val lastListItemPosition = firstListItemPosition + listView.childCount - 1
        if (pos >= firstListItemPosition && pos <= lastListItemPosition) {
            val childIndex = pos - firstListItemPosition
            return listView.getChildAt(childIndex)
        }
        return null
    }

    fun getVisibleRange(): Range<Int> {
        val safeListView = listView
        return if (safeListView == null || safeListView.childCount == 0) {
            Range(0, 0)
        } else {
            Range(safeListView.firstVisiblePosition, safeListView.firstVisiblePosition + safeListView.childCount - 1)
        }
    }

    internal fun updateTableAdapter() {
        val items = ArrayList(feed.items)
        val adapter = RssItemsAdapter(this, items)
        adapter.listener = this
        listView.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean { // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.rss_items, menu)
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

    //
//    override fun loadImage(item: RssItem) {
//        val adapter = listView.adapter as RssItemsAdapter
//        val position = adapter.values.indexOf(item)
//        if (position == -1) {
//            return
//        }
//
//        val image = item.image
//        if (image == null) return
//        //the position is used as a part of task id to handle the same images right
//
//        val task = ImageLoader().buildBitmapTask(taskProvider.threadRunner, image, "_" + Integer.toString(position) + "_"/*, getLoadImageCallback(item, this)*/)
//        val range = visibleRange
//
//        task.taskType = position % 2 + 1
//        task.taskPriority = getTaskPriority(position, range.lower, range.upper - range.lower + 1)
//        task.taskUserData = Pair(position, image)
//        task.addTaskProgressListener(this)
//        task.taskProgressMinChange = 0.2f
//        task.loadPolicy = Task.LoadPolicy.SkipIfAlreadyAdded
//        taskProvider.addTask(task)
//    }
//
//    override fun onProgressChanged(sender: Any, info: ProgressInfo) {
//        val task = sender as Task
//        Assert.assertTrue(task.taskUserData is Pair<*, *>)
//        val taskData = task.taskUserData as Pair<Int, Image>?
//        val view = getViewAtPosition(taskData!!.first)
//
//        if (view != null) { // TODO: move holder access to adapter
//            val holder = view.tag as ViewHolder
//            if (holder.loadingImage === taskData.second) {
//                holder.progressBar.progress = (info.normalizedValue * 100.0f).toInt()
//                //Log.d("imageprogress","progress " + newValue);
//            } else { //Log.d("imageprogress","loadingImage is different");
//            }
//        }
//    }

//    fun onScroll(view: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
//        if (taskProvider.userData is Int) {
//            val distance = Math.abs(taskProvider.userData as Int - firstVisibleItem)
//            if (distance < 5) {
//                return
//            }
//        }
//
//        //TODO: it should be done via api without direct access to getStreamReader
//        taskProvider.threadRunner.launch {
//            val tasks: MutableList<Task> = ArrayList()
//            tasks.addAll(taskProvider.getTasks())
//            for (t in tasks) {
//                val taskData = t.taskUserData as Pair<Int, Image>?
//                val distance = Math.abs(taskProvider.userData as Int - taskData!!.first)
//                if (distance > 30) {
//                    taskManager.cancel(t, null)
//                }
//            }
//        }
//
//        taskProvider.userData = firstVisibleItem
//        taskProvider.updatePriorities(object : PriorityProvider {
//            override fun getPriority(task: Task): Int {
//                Assert.assertTrue(task.taskUserData is Pair<*, *>)
//                val taskData = task.taskUserData as Pair<Int, Image>?
//                val taskPosition = taskData!!.first
//                return getTaskPriority(taskPosition, firstVisibleItem, visibleItemCount)
//            }
//        })
//    }
//
//    private fun getTaskPriority(taskPosition: Int, firstVisibleItem: Int, visibleItemCount: Int): Int { //for a test purpose we start load images from the center of the list view
//        var delta = Math.abs(firstVisibleItem + visibleItemCount / 2 - taskPosition)
//        if (delta > 100) {
//            delta = 100
//        }
//        return 100 - delta
//    }

    companion object {

//        fun getLoadImageCallback(item: RssItem?, activity: RssItemsActivity): ImageLoader.LoadCallback {
//            val ref = WeakReference(activity)
//            return object : ImageLoader.LoadCallback {
//                override fun completed(task: Task?, image: Image?, bitmap: Bitmap?, error: Error?) {
//                    val act = ref.get()
//                    if (act == null || act.isDestroyed || act.isFinishing) {
//                        return
//                    }
//
//                    val adapter = act.listView.adapter as RssItemsAdapter ?: return
//                    val position = adapter.values.indexOf(item)
//                    if (position == -1) {
//                        return
//                    }
//
//                    val view = act.getViewAtPosition(position)
//                    if (view != null) { // TODO: move holder access to adapter
//                        val holder = view.tag as ViewHolder
//                        if (holder.loadingImage === image) {
//                            if (bitmap != null) {
//                                holder.imageView.setImageBitmap(bitmap)
//                            }
//                            holder.loadingImage = null
//                            holder.progressBar.visibility = View.INVISIBLE
//                        }
//                    }
//                }
//            }
//        }
    }
}