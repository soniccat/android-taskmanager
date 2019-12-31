package com.rssclient.rssfeeditems.vm

import androidx.lifecycle.LiveData
import com.aglushkov.repository.livedata.Resource
import com.example.alexeyglushkov.taskmanager.task.TaskManagerSnapshot
import com.rssclient.model.RssItem

interface RssItemsViewModelContract {
    val rssItems: LiveData<Resource<RssItem>>
    val taskManagerSnapshot: LiveData<TaskManagerSnapshot>
}