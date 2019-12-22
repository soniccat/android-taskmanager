package com.aglushkov.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.aglushkov.repository.command.RepositoryCommand
import java.util.*

/*
    RepositoryCommandHolder keeps relation between LiveData objects and commands related to them e.g: loading
    It's might be useful when you don't want to create a new LiveData on every command and get a
    ready one if it exists
    Also with putLiveData you can set an empty LiveData in advance to be able to start observing before
    an actual command was run. Then while creating a RepositoryCommand you can retrieve the LiveData
    and pass to the Command constructor.
    WeakHashMap is used not to retain your LiveData. Only ViewModel is allowed to retain them. That will
    release LiveData objects after leaving screens.
 */
class RepositoryCommandHolder {
    private val liveDataIdMap = WeakHashMap<LiveData<*>, Long>()
    private val liveDataCommandMap = WeakHashMap<LiveData<*>, RepositoryCommand<*>>()

    fun <T : RepositoryCommand<*>> putCommand(cmd: T): T {
        val oldCmd = getCommand<RepositoryCommand<*>>(cmd.commandId)
        if (oldCmd != null) {
            cancel(oldCmd.liveData)
        }

        liveDataIdMap[cmd.liveData] = cmd.commandId
        liveDataCommandMap[cmd.liveData] = cmd
        return cmd
    }

    fun getCommand(liveData: LiveData<*>): RepositoryCommand<*>? {
        return liveDataCommandMap[liveData]
    }

    fun <T : RepositoryCommand<*>> getCommand(id: Long): T? {
        var cmd: T? = null
        for (c in liveDataCommandMap.values) {
            if (c != null && c.commandId == id) {
                cmd = c as T
            }
        }
        return cmd
    }

    fun <T> ensureLiveData(id: Long, default: T? = null): MutableLiveData<T> {
        var liveData = getLiveData<MutableLiveData<T>>(id)
        if (liveData == null) {
            liveData = MutableLiveData<T>()
            if (default != null) {
                liveData.postValue(default)
            }
            putLiveData(id, liveData)
        }
        return liveData
    }

    fun <T : LiveData<*>> getLiveData(id: Long): T? {
        var result: T? = null
        for ((key, value) in liveDataIdMap) {
            if (value == id) {
                result = key as T
                break
            }
        }
        return result
    }

    fun putLiveData(id: Long, liveData: LiveData<*>) {
        cancel(liveData)
        liveDataIdMap[liveData] = id
        liveDataCommandMap[liveData] = null
    }

    fun cancel(liveData: LiveData<*>) {
        val cmd = getCommand(liveData)
        canceIfNeeded(cmd, liveData)
    }

    private fun canceIfNeeded(cmd: RepositoryCommand<*>?, liveData: LiveData<*>) {
        if (cmd != null) {
            cmd.cancel()
            liveDataIdMap.remove(liveData)
            liveDataCommandMap.remove(liveData)
        }
    }
}