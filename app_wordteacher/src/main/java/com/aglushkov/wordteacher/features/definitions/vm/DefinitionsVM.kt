package com.aglushkov.wordteacher.features.definitions.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.aglushkov.modelcore.extensions.getString
import com.aglushkov.modelcore.resource.*
import com.aglushkov.modelcore_ui.extensions.getErrorString
import com.aglushkov.wordteacher.di.AppComponentOwner
import com.aglushkov.wordteacher.features.definitions.repository.WordRepository
import com.aglushkov.modelcore_ui.view.BaseViewItem
import com.aglushkov.wordteacher.R
import com.aglushkov.wordteacher.model.WordTeacherWord
import com.aglushkov.wordteacher.model.toString
import kotlinx.coroutines.flow.*

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

    fun onWordSubmitted(word: String) {
        this.word?.let {
            wordRepository.clear(it)
        }

        if (word.isNotEmpty()) {
            innerDefinitions.value = Resource.Uninitialized()
            load(word)
        }
    }

    fun onTryAgainClicked() {
        load(word!!)
    }

    // Actions

    private fun load(word: String) {
        this.word = word
        innerDefinitions.load(wordRepository.scope, true) {
            // TODO: handle Loading to show intermediate results
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
        items.add(DefinitionsDisplayModeViewItem(listOf(DefinitionsDisplayMode.BySource, DefinitionsDisplayMode.Merged), 0))
        items.add(WordDividerViewItem())

        for (word in words) {
            addWordViewItems(word, items)
            items.add(WordDividerViewItem())
        }

        return items
    }

    private fun addWordViewItems(word: WordTeacherWord, items: MutableList<BaseViewItem<*>>) {
        items.add(WordTitleViewItem(word.word))
        word.transcription?.let {
            items.add(WordTranscriptionViewItem(it))
        }

        for (partOfSpeech in word.definitions.keys) {
            items.add(WordPartOfSpeechViewItem(partOfSpeech.toString(getApplication())))

            for (def in word.definitions[partOfSpeech].orEmpty()) {
                items.add(WordDefinitionViewItem(def.definition))

                if (def.examples.isNotEmpty()) {
                    items.add(WordSubHeaderViewItem(getString(R.string.word_section_examples)))
                    for (ex in def.examples) {
                        items.add(WordExampleViewItem(ex))
                    }
                }

                if (def.synonyms.isNotEmpty()) {
                    items.add(WordSubHeaderViewItem(getString(R.string.word_section_synonyms)))
                    for (synonym in def.synonyms) {
                        items.add(WordSynonymViewItem(synonym))
                    }
                }
            }
        }
    }

    fun getErrorText(res: Resource<*>): String? {
        val hasConnection = appComponent.getConnectivityManager().isDeviceOnline
        val hasResponse = true // TODO: handle error server response
        return res.getErrorString(getApplication(), hasConnection, hasResponse)
    }
}