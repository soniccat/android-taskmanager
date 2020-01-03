package com.rssclient.rssfeeditems.vm

import android.graphics.Bitmap
import android.os.Bundle
import androidx.lifecycle.*
import com.aglushkov.repository.livedata.Resource
import com.aglushkov.taskmanager_http.image.ImageLoader
import com.example.alexeyglushkov.streamlib.progress.ProgressInfo
import com.example.alexeyglushkov.streamlib.progress.ProgressListener
import com.example.alexeyglushkov.taskmanager.task.*
import com.main.MainApplication
import com.rssclient.model.RssFeed
import com.rssclient.model.RssItem
import com.rssclient.vm.RssView
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import java.lang.NullPointerException

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

    override val rssItems: LiveData<Resource<List<RssView<*>>>>
        get() = rssRepository.getFeedLiveData(getRootFeed()!!.url).map {
            val items = it.data()?.items ?: emptyList()
            val convertedData: List<RssView<*>> = items.map { item -> RssView.RssItemView(item) }
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

    override fun resetImageProgress(imageTag: String) {
        getImageProgressLiveData(imageTag).value = 0.0f
    }

    override fun onSnapshotChanged(snapshot: TaskManagerSnapshot) {
        taskManagerSnapshot.value = snapshot
    }

    override fun onRssItemPressed(rssItem: RssItem) {

    }

    override fun onLoadImageRequested(imageInfo: RssItemsViewModelContract.ImageInfo, completion: (bitmap: Bitmap?, error: Exception?) -> Unit) {
        val range = imageInfo.visibleRange
        val position = imageInfo.position

        val task = ImageLoader().buildBitmapTask(taskProvider.threadRunner,
                imageInfo.image,
                "_" + Integer.toString(position))
        task.taskType = position % 2 + 1
        task.taskPriority = getTaskPriority(position, range.lower, range.upper - range.lower + 1)
        task.taskUserData = imageInfo.imageTag
        task.addTaskProgressListener(this)
        task.taskProgressMinChange = 0.2f
        task.loadPolicy = Task.LoadPolicy.CompleteWhenAlreadyAddedCompletes

        viewModelScope.launch {
            try {
                val bitmap = Tasks.run<Bitmap>(task, taskProvider)
                completion(bitmap, null)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                completion(null, e)
            }
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

    override fun onProgressChanged(sender: Any?, progressInfo: ProgressInfo) {
        val task = sender as Task
        val imageTag = task.taskUserData as String

        val liveData = getImageProgressLiveData(imageTag)
        liveData.postValue(progressInfo.normalizedValue)

//        val view = getViewAtPosition(taskData!!.first)
//
//        if (view != null) { // TODO: move holder access to adapter
//            val holder = view.tag as ViewHolder
//            if (holder.loadingImage === taskData.second) {
//                holder.progressBar.progress = (progressInfo.normalizedValue * 100.0f).toInt()
//                //Log.d("imageprogress","progress " + newValue);
//            } else { //Log.d("imageprogress","loadingImage is different");
//            }
//        }
    }
}