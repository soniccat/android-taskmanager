package com.aglushkov.wordteacher.features.definitions.vm

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.aglushkov.modelcore.resource.*
import com.aglushkov.modelcore_ui.extensions.getErrorString
import com.aglushkov.wordteacher.di.AppComponentOwner
import com.aglushkov.wordteacher.features.definitions.repository.WordRepository
import com.aglushkov.modelcore_ui.view.BaseViewItem
import com.aglushkov.wordteacher.model.WordTeacherWord
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DefinitionsVM(app: Application,
                    private val state: SavedStateHandle): AndroidViewModel(app) {
    private val appComponent = (app as AppComponentOwner).appComponent
    private val wordRepository: WordRepository = appComponent.getWordRepository()

    private val innerDefinitions = MutableLiveData<Resource<List<BaseViewItem<*>>>>(Resource.Uninitialized())
    val definitions: LiveData<Resource<List<BaseViewItem<*>>>> = innerDefinitions

    // State
    var word: String?
        get() {
            return state["word"]
        }
        set(value) {
            state["word"] = value
        }

    init {
        word?.let {
            load(it)
        } ?: run {
            load("owl")
        }
    }

    // Events

    fun onTryAgainClicked() {
        load(word!!)
    }

    // Actions

    private fun load(word: String) {
        this.word = word
        innerDefinitions.load(wordRepository.scope, true) {
            buildViewItems(wordRepository.define(word).flow.first {
                if (it is Resource.Error) {
                    throw it.throwable
                }

                it.isLoaded()
            }.data()!!)
        }
    }

    private fun buildViewItems(words: List<WordTeacherWord>): List<BaseViewItem<*>> {
        val items = mutableListOf<BaseViewItem<*>>()
        for (word in words) {
            items.add(WordTitleViewItem(word.word))
            word.transcription?.let {
                items.add(WordTranscriptionViewItem(it))
            }
        }

        return items
    }

    fun getErrorText(res: Resource<*>): String? {
        val hasConnection = appComponent.getConnectivityManager().isDeviceOnline
        val hasResponse = true // TODO: handle error server response
        return res.getErrorString(getApplication(), hasConnection, hasResponse)
    }
}