package com.aglushkov.wordteacher.features.definitions.vm

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.aglushkov.wordteacher.di.AppComponentOwner
import com.aglushkov.wordteacher.features.definitions.repository.WordRepository
import com.aglushkov.general.view.BaseViewItem
import com.aglushkov.general.resource.Resource
import com.aglushkov.general.resource.getErrorString
import com.aglushkov.general.resource.isLoaded
import com.aglushkov.general.resource.load
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class DefinitionsVM(app: Application,
                    private val state: SavedStateHandle): AndroidViewModel(app) {
    private val appComponent = (app as AppComponentOwner).appComponent
    private val wordRepository: WordRepository = appComponent.getWordRepository()

    private val innerDefinitions = MutableLiveData<Resource<List<BaseViewItem<*>>>>(Resource.Uninitialized())
    val definitions: LiveData<Resource<List<BaseViewItem<*>>>> = innerDefinitions

    init {
        load("owl")

        viewModelScope.launch {
            appComponent.getConnectivityManager().flow.collect {
                Log.d("a", "b" + it)
            }
        }
    }

    private fun load(word: String) {
        innerDefinitions.load(wordRepository.scope, true) {
            wordRepository.define(word).flow.first {
                it.isLoaded()
            }.data()!!.map {
                WordViewItem(it)
            }
        }
    }

    fun getErrorText(res: Resource<*>): String? {
        val hasConnection = appComponent.getConnectivityManager().isDeviceOnline
        val hasResponse = true // TODO: handle error server response
        return res.getErrorString(getApplication(), hasConnection, hasResponse)
    }
}