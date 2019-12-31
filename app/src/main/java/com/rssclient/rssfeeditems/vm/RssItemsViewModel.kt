package com.rssclient.rssfeeditems.vm

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.aglushkov.repository.livedata.Resource
import com.example.alexeyglushkov.taskmanager.task.PriorityTaskProvider
import com.example.alexeyglushkov.taskmanager.task.SimpleTaskManagerSnapshot
import com.example.alexeyglushkov.taskmanager.task.TaskManagerSnapshot
import com.main.MainApplication
import com.rssclient.model.RssFeed
import com.rssclient.model.RssItem
import java.lang.NullPointerException

class RssItemsViewModel(application: MainApplication, val args: Bundle):
        AndroidViewModel(application), RssItemsViewModelContract, TaskManagerSnapshot.OnSnapshotChangedListener {
    companion object {
        val FeedKey = "FeedKey"
        val TaskProviderId = "ImageTaskProvider"
    }

    private var rssRepository = application.rssRepository
    private var taskManager = application.taskManager
    private var taskProvider: PriorityTaskProvider
    private var snapshot: TaskManagerSnapshot

    override val rssItems = MutableLiveData<Resource<RssItem>>()
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

    override fun onSnapshotChanged(snapshot: TaskManagerSnapshot) {
        taskManagerSnapshot.value = snapshot
    }
}