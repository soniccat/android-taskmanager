package com.example.alexeyglushkov.cachemanager.preference

import android.content.Context
import android.content.SharedPreferences
import com.example.alexeyglushkov.cachemanager.Storage
import com.example.alexeyglushkov.cachemanager.StorageEntry
import com.example.alexeyglushkov.cachemanager.StorageMetadata
import com.example.alexeyglushkov.tools.ContextProvider

/**
 * Created by alexeyglushkov on 04.09.16.
 */
class PreferenceStorage(private val name: String, private val contextProvider: ContextProvider) : Storage {
    @Throws(Exception::class)
    override fun put(key: String, value: Any, metadata: StorageMetadata?) {
        val editor = writePreference
        if (value is Int) {
            editor.putInt(key, value)
        } else if (value is Long) {
            editor.putLong(key, value)
        }
        editor.commit()
    }

    override fun getValue(key: String): Any? {
        return readPreference.all[key]
    }

    override fun createMetadata(): StorageMetadata {
        throw NotImplementedError("StorageMetadata isn't supported in PreferenceStorage")
    }

    override fun getMetadata(key: String): StorageMetadata? {
        return null
    }

    @Throws(Exception::class)
    override fun remove(key: String) {
        val editor = writePreference
        editor.remove(key)
        editor.commit()
    }

    override fun getEntry(key: String): StorageEntry? {
        return null
    }

    override fun getEntries(): List<StorageEntry> {
        return emptyList()
    }

    @Throws(Exception::class)
    override fun removeAll() {
        val editor = writePreference
        editor.clear()
        editor.commit()
    }

    ////
    private val writePreference: SharedPreferences.Editor
        private get() = context.getSharedPreferences(name, Context.MODE_PRIVATE).edit()

    private val readPreference: SharedPreferences
        private get() = context.getSharedPreferences(name, Context.MODE_PRIVATE)

    private val context: Context
        private get() = contextProvider.context

}