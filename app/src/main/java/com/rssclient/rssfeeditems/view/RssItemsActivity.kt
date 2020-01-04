package com.rssclient.rssfeeditems.view

import android.graphics.Bitmap
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
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
import com.rssclient.vm.RssViewItem
import java.lang.Exception
import java.lang.NullPointerException

class RssItemsActivity : AppCompatActivity() {
    private lateinit var vm: RssItemsViewModelContract
    private var recyclerView: RecyclerView? = null
    private var taskManagerView: TaskManagerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        vm = ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
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
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                vm.onScrolled(getVisibleRange())
            }
        })
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

    private fun handleDataChange(it: Resource<List<RssViewItem<*>>>) {
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

    private fun showData(data: List<RssViewItem<*>>?) {
        val adapter = recyclerView?.adapter as? RssFeedsAdapter
        if (adapter == null) {
            createAdapter(data)
        } else {
            adapter.submitList(data)
        }
    }

    private fun createAdapter(data: List<RssViewItem<*>>?) {
        val safeListView = recyclerView ?: throw NullPointerException("ListView is null")

        val imageBinder = ImageBinder(object : ImageBinder.ImageLoader {
            override fun loadImage(image: Image, params: Map<String, Any>, completion: (bitmap: Bitmap?, error: Exception?) -> Unit) {
                recyclerView?.let { view ->
                    val vh = params[RssItemBinder.ViewHolderKey] as RssItemsAdapter.RssItemsViewHolder
                    val imageTag = params[RssItemBinder.ImageTagKey] as String
                    val imageInfo = RssItemsViewModelContract.ImageInfo(image, imageTag, vh.bindPosition, getVisibleRange())

                    this@RssItemsActivity.vm.onLoadImageRequested(imageInfo, completion)
                }
            }
        })

        val itemBinder = RssItemBinder(imageBinder).apply {
            listener = object : RssItemBinder.Listener {
                override fun onClick(item: RssItem) {
                    this@RssItemsActivity.vm.onRssItemPressed(item)
                }

                override fun bindImageProgress(viewHolder: RssItemsAdapter.RssItemsViewHolder, imageTag: String) {
                    vm.getImageProgressLiveData(imageTag).observe(this@RssItemsActivity, Observer {
                        viewHolder.showProgress(it)
                    })
                }

                override fun unbindImageProgress(imageTag: String) {
                    vm.getImageProgressLiveData(imageTag).removeObservers(this@RssItemsActivity)
                }
            }
        }

        val adapter = RssItemsAdapter(itemBinder)
        adapter.submitList(data)

        safeListView.adapter = adapter
    }

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
        menuInflater.inflate(R.menu.rss_items, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        return if (id == R.id.action_settings) {
            true
        } else super.onOptionsItemSelected(item)
    }
}