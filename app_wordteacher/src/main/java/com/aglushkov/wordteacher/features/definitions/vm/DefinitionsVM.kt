package com.aglushkov.wordteacher.features.definitions.vm

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import com.aglushkov.wordteacher.di.AppComponentOwner
import com.aglushkov.wordteacher.features.definitions.repository.WordRepository
import com.aglushkov.wordteacher.model.isLoaded
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class DefinitionsVM(app: Application,
                    private val state: SavedStateHandle): AndroidViewModel(app) {
    private val wordRepository: WordRepository = (app as AppComponentOwner).appComponent.getWordRepository()

    init {
        wordRepository.scope.launch {
            val d = wordRepository.define("owl").flow.first { it.isLoaded() }
            Log.d("t", d.toString())
        }
    }
}