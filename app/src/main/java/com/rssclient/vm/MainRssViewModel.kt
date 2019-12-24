package com.rssclient.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.aglushkov.repository.livedata.Resource
import com.example.alexeyglushkov.taskmanager.task.TaskManager
import com.main.MainApplication
import com.rssclient.model.RssFeed
import com.rssclient.model.RssFeedRepository

class MainRssViewModel(application: MainApplication): AndroidViewModel(application) {
    private var taskManager = application.taskManager
    private var rssRepository = application.rssRepository

    val feedLiveData: LiveData<Resource<List<RssFeed>>>
        get() = rssRepository.getFeedsLiveData()

    init {

    }
}