package com.example.alexeyglushkov.quizletservice

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.aglushkov.repository.RepositoryCommandHolder
import com.aglushkov.repository.command.CancellableRepositoryCommand
import com.aglushkov.repository.command.DisposableRepositoryCommand
import com.aglushkov.repository.command.RepositoryCommand
import com.aglushkov.repository.livedata.NonNullMutableLiveData
import com.aglushkov.repository.livedata.Resource
import com.aglushkov.repository.livedata.Resource.Uninitialized
import com.aglushkov.repository.livedata.ResourceLiveDataProvider
import com.example.alexeyglushkov.authtaskmanager.BaseServiceTask
import com.example.alexeyglushkov.cachemanager.Storage
import com.example.alexeyglushkov.cachemanager.clients.*
import com.example.alexeyglushkov.quizletservice.entities.QuizletSet
import com.example.alexeyglushkov.quizletservice.entities.QuizletTerm
import com.example.alexeyglushkov.streamlib.progress.ProgressListener
import io.reactivex.Single
import io.reactivex.internal.functions.Functions
import kotlinx.coroutines.*
import java.lang.Exception
import kotlin.collections.ArrayList

// TODO: base class for repository with service and cache
class QuizletRepository(private val service: QuizletService, storage: Storage) : ResourceLiveDataProvider<List<QuizletSet>> {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val cache: ScopeCache
    private val commandHolder = RepositoryCommandHolder<Long>()

    init {
        cache = ScopeCacheAdapter(SimpleCache(storage, 0), scope)
    }

    //// Actions
    fun loadSets(progressListener: ProgressListener): RepositoryCommand<Resource<List<QuizletSet>>, Long> {
        val job = scope.launch { loadSetsInternal(progressListener) }
        return commandHolder.putCommand(CancellableRepositoryCommand(LOAD_SETS_COMMAND_ID, job, setsLiveData))
    }

    @Throws
    suspend private fun loadSetsInternal(progressListener: ProgressListener) {
        val previousState = setsLiveData.value!!
        setsLiveData.value = setsLiveData.value!!.toLoading()

        try {
            val sets = service.loadSets(progressListener)
            cache.putValue("quizlet_sets", sets)
            setsLiveData.value = setsLiveData.value!!.toLoaded(sets)
        } catch (e: Exception) {
            if (e is CancellationException) {
                setsLiveData.value = setsLiveData.value!!.toError(e, true, previousState.data(), previousState.canLoadNextPage)
            } else {
                setsLiveData.value = previousState
            }
            throw e
        }
    }

    fun restoreOrLoad(progressListener: ProgressListener): RepositoryCommand<Resource<List<QuizletSet>>, Long> {
        val job = scope.launch { restoreOrLoadInternal(progressListener) }
        return commandHolder.putCommand(CancellableRepositoryCommand(LOAD_SETS_COMMAND_ID, job, setsLiveData))
    }

    private suspend fun restoreOrLoadInternal(progressListener: ProgressListener) {
        val previousState: Resource<List<QuizletSet>> = setsLiveData.value!!
        setsLiveData.setValue(previousState.toLoading())

        var sets: List<QuizletSet>? = null
        try {
            try {
                sets = cache.getCachedValue<List<QuizletSet>>("quizlet_sets")
            } catch (e: Exception) {
                // ignore
            }

            if (sets != null) {
                setsLiveData.setValue(setsLiveData.value!!.toLoaded(sets))
            } else {
                setsLiveData.setValue(previousState)
                if (service.account?.isAuthorized ?: false) {
                    loadSetsInternal(progressListener)
                } else {
                    throw Exception("restoreOrLoadInternal: Account isn't aurthoized to loadSetsInternal")
                }
            }
        } catch (e: Exception) {
            if (e is CancellationException) {
                setsLiveData.setValue(previousState)
            }
        }
    }

    //// Setters / Getters
    // Getters
    override val liveData: LiveData<Resource<List<QuizletSet>>>
        get() = setsLiveData

    fun getTermListLiveData(setId: Long): MutableLiveData<Resource<List<QuizletTerm>>> {
        val liveDataId = LOAD_TERMS_COMMAND_PREFIX + setId
        var liveData = commandHolder.getLiveData<MutableLiveData<Resource<List<QuizletTerm>>>>(liveDataId)
        if (liveData == null) {
            liveData = QuizletTermAdapter(setId).liveData
            commandHolder.putLiveData(liveDataId, liveData)
        }
        return liveData
    }

    private val setsLiveData: NonNullMutableLiveData<Resource<List<QuizletSet>>>
        get() {
            var liveData = commandHolder.getLiveData<NonNullMutableLiveData<Resource<List<QuizletSet>>>>(LOAD_SETS_COMMAND_ID)
            if (liveData == null) {
                liveData = NonNullMutableLiveData(Uninitialized<List<QuizletSet>>() as Resource<List<QuizletSet>>)
                commandHolder.putLiveData(LOAD_SETS_COMMAND_ID, liveData)
            }
            return liveData
        }

    // Inner Classes
// QuizletSet liveData to QuizletTerm liveData
    private inner class QuizletTermAdapter(private val setId: Long) : ResourceLiveDataProvider<List<QuizletTerm>> {
        override val liveData: MutableLiveData<Resource<List<QuizletTerm>>>
            get() {
                val mediatorLiveData = MediatorLiveData<Resource<List<QuizletTerm>>>()
                mediatorLiveData.setValue(Uninitialized())
                mediatorLiveData.addSource<Resource<List<QuizletSet>>>(this@QuizletRepository.liveData, object : Observer<Resource<List<QuizletSet>>> {
                    override fun onChanged(listResource: Resource<List<QuizletSet>>) {
                        mediatorLiveData.setValue(buildFinalResource(listResource))
                    }
                })
                return mediatorLiveData
            }

        private fun buildFinalResource(listResource: Resource<List<QuizletSet>>): Resource<List<QuizletTerm>> {
            val terms: MutableList<QuizletTerm> = ArrayList()
            val data = listResource.data()
            if (data != null) {
                for (set in data) {
                    for (term in set.terms) {
                        val setId = term.setId
                        if (this.setId == Companion.NO_ID || setId == this.setId) {
                            terms.add(term)
                        }
                    }
                }
            }
            return listResource.copyWith(terms)
        }
    }

    companion object {
        private const val LOAD_SETS_COMMAND_ID: Long = 0
        private const val LOAD_TERMS_COMMAND_PREFIX: Long = 2 // it's 2 to support -1 set id

        private const val NO_ID: Long = -1
    }
}