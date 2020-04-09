package com.rssclient.rssfeeditems.vm

import android.graphics.Bitmap
import android.os.Bundle
import androidx.lifecycle.*
import com.aglushkov.modelcore.resource.Resource
import com.aglushkov.taskmanager_http.image.ImageTask
import com.example.alexeyglushkov.streamlib.progress.ProgressInfo
import com.example.alexeyglushkov.streamlib.progress.ProgressListener
import com.example.alexeyglushkov.taskmanager.pool.start
import com.example.alexeyglushkov.taskmanager.task.*
import com.example.alexeyglushkov.taskmanager.providers.PriorityTaskProvider
import com.example.alexeyglushkov.taskmanager.snapshot.SimpleTaskManagerSnapshot
import com.example.alexeyglushkov.taskmanager.snapshot.TaskManagerSnapshot
import com.example.alexeyglushkov.tools.Range
import com.main.MainApplication
import com.rssclient.model.RssFeed
import com.rssclient.model.RssItem
import com.rssclient.RssViewItem
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import java.lang.NullPointerException
import kotlin.math.abs

class RssItemsViewModel(application: MainApplication, val args: Bundle):
        AndroidViewModel(application),
        RssItemsViewModelContract,
        ProgressListener,
        TaskManagerSnapshot.OnSnapshotChangedListener {

    companion object {
        val FeedKey = "FeedKey"
        val TaskProviderId = "ImageTaskProvider"
    }

    private var rssRepository = application.rssRepository
    private var taskManager = application.taskManager
    private var taskProvider: PriorityTaskProvider
    private var snapshot: TaskManagerSnapshot

    override val rssItems: LiveData<Resource<List<RssViewItem<*>>>>
        get() = rssRepository.getFeedLiveData(getRootFeed()!!.url).map {
            val items = it.data()?.items ?: emptyList()
            val convertedData: List<RssViewItem<*>> = items.map { item -> RssViewItem.RssItemViewItem(item) }
            it.copyWith(convertedData)
        }

    override val taskManagerSnapshot = MutableLiveData<TaskManagerSnapshot>()

    init {
        val feed = getRootFeed() ?: throw NullPointerException("FeedKey is empty")
        rssRepository.loadRssFeed(feed.url, null)

        taskProvider = PriorityTaskProvider(taskManager.threadRunner, TaskProviderId + feed.url.hashCode())
        taskManager.addTaskProvider(taskProvider)

        snapshot = SimpleTaskManagerSnapshot()
        snapshot.startSnapshotRecording(taskManager)
        snapshot.addSnapshotListener(this)
    }

    override fun onCleared() {
        super.onCleared()
        taskManager.removeTaskProvider(taskProvider)
    }

    private fun getRootFeed(): RssFeed? {
        return args.getParcelable(FeedKey)
    }

    override fun getImageProgressLiveData(imageTag: String): MutableLiveData<Float> {
        return rssRepository.getImageProgressLiveData(imageTag)
    }

    private fun resetImageProgress(imageTag: String) {
        getImageProgressLiveData(imageTag).value = 0.0f
    }

    override fun onSnapshotChanged(snapshot: TaskManagerSnapshot) {
        taskManagerSnapshot.value = snapshot
    }

    override fun onRssItemPressed(rssItem: RssItem) {
    }

    override fun onLoadImageRequested(imageInfo: RssItemsViewModelContract.ImageInfo, completion: (bitmap: Bitmap?, error: Exception?) -> Unit) {
        resetImageProgress(imageInfo.imageTag)

        val imageTask = createImageTask(imageInfo)
        viewModelScope.launch {
            try {
                val bitmap = taskProvider.start<Bitmap>(imageTask)
                completion(bitmap, null)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                completion(null, e)
            }
        }
    }

    override fun onProgressChanged(sender: Any?, progressInfo: ProgressInfo) {
        if (sender is RssImageTask) {
            val liveData = getImageProgressLiveData(sender.imageTag)
            liveData.postValue(progressInfo.normalizedValue)
        }
    }

    override fun onScrolled(visibleRange: Range<Int>) {
        if (taskProvider.userData is Int) {
            val distance = Math.abs(taskProvider.userData as Int - visibleRange.lower)
            if (distance < 5) {
                return
            }
        }

        //TODO: it should be done via api without direct access to getStreamReader
        taskProvider.threadRunner.launch {
            val tasks: MutableList<Task> = ArrayList()
            tasks.addAll(taskProvider.getTasks())
            for (task in tasks) {
                if (task is RssImageTask) {
                    val distance = abs(taskProvider.userData as Int - task.position)
                    if (distance > 30) {
                        taskManager.cancel(task, null)
                    }
                }
            }
        }

        taskProvider.userData = visibleRange.lower
        taskProvider.updatePriorities(object : PriorityTaskProvider.PriorityProvider {
            override fun getPriority(task: Task): Int {
                return if (task is RssImageTask) {
                    getTaskPriority(task.position, visibleRange.lower, visibleRange.length)
                } else {
                    task.taskPriority
                }
            }
        })
    }

    private fun createImageTask(imageInfo: RssItemsViewModelContract.ImageInfo): ImageTask {
        return RssImageTask(imageInfo).apply {
            val range = imageInfo.visibleRange
            taskPriority = getTaskPriority(position, range.lower, range.length)
            addTaskProgressListener(this@RssItemsViewModel)
        }
    }

    private fun getTaskPriority(taskPosition: Int, firstVisibleItem: Int, visibleItemCount: Int): Int {
        //for a test purpose we start load images from the center of the list view
        var delta = Math.abs(firstVisibleItem + visibleItemCount / 2 - taskPosition)
        if (delta > 100) {
            delta = 100
        }
        return 100 - delta
    }

    private class RssImageTask(imageInfo: RssItemsViewModelContract.ImageInfo): ImageTask(imageInfo.image) {
        var position = imageInfo.position
        var imageTag = imageInfo.imageTag

        init {
            task.taskType = position % 2 + 1
            task.taskProgressMinChange = 0.2f
            task.loadPolicy = Task.LoadPolicy.CompleteWhenAlreadyAddedCompletes
        }
    }
}