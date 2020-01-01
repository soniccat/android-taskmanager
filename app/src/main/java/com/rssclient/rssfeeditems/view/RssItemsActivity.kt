package com.rssclient.rssfeeditems.view

import android.graphics.Bitmap
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AbsListView
import android.widget.AbsListView.OnScrollListener
import android.widget.ListView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aglushkov.repository.livedata.Resource
import com.aglushkov.taskmanager_http.image.Image
import com.aglushkov.taskmanager_http.image.ImageBinder
import com.example.alexeyglushkov.taskmanager.ui.TaskManagerView
import com.example.alexeyglushkov.tools.Range
import com.main.MainApplication
import com.rssclient.controllers.R
import com.rssclient.model.RssItem
import com.rssclient.rssfeed.view.RssFeedsAdapter
import com.rssclient.rssfeeditems.vm.RssItemsViewModel
import com.rssclient.rssfeeditems.vm.RssItemsViewModelContract
import com.rssclient.vm.RssView
import kotlinx.android.synthetic.main.settings_text_field.view.*
import java.lang.Exception
import java.lang.NullPointerException

class RssItemsActivity : AppCompatActivity() {
    private lateinit var vm: RssItemsViewModelContract
    private var recyclerView: RecyclerView? = null
    private var taskManagerView: TaskManagerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        vm = ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return RssItemsViewModel(MainApplication.instance, intent.extras!!) as T
            }
        }).get(RssItemsViewModel::class.java)
        observeViewModel()

        setContentView(R.layout.activity_rss_items)
        bindView()
    }

    private fun bindView() {
        taskManagerView = findViewById<View>(R.id.task_manager_view) as TaskManagerView
        val recyclerView = findViewById<View>(R.id.list) as RecyclerView
        this.recyclerView = recyclerView

        recyclerView.layoutManager = LinearLayoutManager(recyclerView.context, RecyclerView.VERTICAL, false)

//        recyclerView.setOnScrollListener(object : OnScrollListener {
//            override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {}
//            override fun onScroll(view: AbsListView, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
//                val adapter = view.adapter as? RssItemsAdapter
//                if (adapter != null) {
//                    this@RssItemsActivity.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount)
//                }
//            }
//        })
    }

    override fun onDestroy() {
        super.onDestroy()
        recyclerView = null
        taskManagerView = null
    }

    //// Actions

    private fun observeViewModel() {
        vm.rssItems.observe(this, Observer {
            if (it == null) return@Observer
            handleDataChange(it)
        })

        vm.taskManagerSnapshot.observe(this, Observer {
            if (it == null) return@Observer
            taskManagerView?.showSnapshot(it)
        })
    }

    private fun handleDataChange(it: Resource<List<RssView<*>>>) {
        val data = it.data()
        showData(data)

        if (data.isNullOrEmpty()) {
            if (data is Resource.Loading<*>) {
                // TODO: show loading
            } else if (data is Resource.Error<*>) {
                // TODO: show error
            }
        }
    }

    private fun showData(data: List<RssView<*>>?) {
        val adapter = recyclerView?.adapter as? RssFeedsAdapter
        if (adapter == null) {
            createAdapter(data)
        } else {
            adapter.submitList(data)
        }
    }

    private fun createAdapter(data: List<RssView<*>>?) {
        val safeListView = recyclerView ?: throw NullPointerException("ListView is null")

        val imageBinder = ImageBinder(object : ImageBinder.ImageLoader {
            override fun loadImage(image: Image, params: Map<String, Any>, completion: (bitmap: Bitmap?, error: Exception?) -> Unit) {
                recyclerView?.let { view ->
                    val cell = params[RssItemBinder.CellViewKey] as View
                    val imageTag = params[RssItemBinder.ImageTag] as Int

                    val position = view.getChildAdapterPosition(cell)
                    val imageInfo = RssItemsViewModelContract.ImageInfo(image, imageTag, position, getVisibleRange())

                    this@RssItemsActivity.vm.onLoadImageRequested(imageInfo, completion)
                }
            }
        })

        val itemBinder = RssItemBinder(imageBinder).apply {
            listener = object : RssItemBinder.Listener {
                override fun onClick(item: RssItem) {
                    this@RssItemsActivity.vm.onRssItemPressed(item)
                }

                override fun bindImageProgress(progressBar: ProgressBar, tag: Int) {
                    vm.getImageProgressLiveData(tag).observe(this@RssItemsActivity, Observer {
                        progressBar.progress = (it * 100.0f).toInt()
                    })
                }

                override fun unbindImageProgress(tag: Int) {
                    vm.getImageProgressLiveData(tag).removeObservers(this@RssItemsActivity)
                }
            }
        }

        val adapter = RssItemsAdapter(itemBinder)
        adapter.submitList(data)

        safeListView.adapter = adapter
    }

//    fun getViewAtPosition(pos: Int): View? {
//        val firstListItemPosition = listView.firstVisiblePosition
//        val lastListItemPosition = firstListItemPosition + listView.childCount - 1
//        if (pos >= firstListItemPosition && pos <= lastListItemPosition) {
//            val childIndex = pos - firstListItemPosition
//            return listView.getChildAt(childIndex)
//        }
//        return null
//    }

    fun getVisibleRange(): Range<Int> {
        val safeListView = recyclerView
        return if (safeListView == null || safeListView.childCount == 0) {
            Range(0, 0)
        } else {
            val layout = safeListView.layoutManager as LinearLayoutManager
            Range(layout.findFirstVisibleItemPosition(), layout.findLastVisibleItemPosition())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.rss_items, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
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


//    companion object {
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
//    }
}