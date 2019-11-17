package com.rssclient.model

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import com.aglushkov.taskmanager_http.loader.http.HTTPConnectionStreamReader
import com.aglushkov.taskmanager_http.loader.http.HTTPConnectionStreamReaderAdaptor
import com.aglushkov.taskmanager_http.loader.http.HttpTaskTransport
import com.aglushkov.taskmanager_http.loader.http.TransportTask
import com.example.alexeyglushkov.streamlib.data_readers_and_writers.ByteArrayReader
import com.example.alexeyglushkov.streamlib.data_readers_and_writers.InputStreamDataReader
import com.example.alexeyglushkov.streamlib.data_readers_and_writers.ObjectReader
import com.example.alexeyglushkov.streamlib.data_readers_and_writers.ObjectWriter
import com.example.alexeyglushkov.taskmanager.file.FileKeepTask
import com.example.alexeyglushkov.taskmanager.file.FileLoadTask
import com.example.alexeyglushkov.taskmanager.task.Task
import com.example.alexeyglushkov.taskmanager.task.Task.Callback
import com.example.alexeyglushkov.taskmanager.task.Task.Status
import com.example.alexeyglushkov.taskmanager.task.TaskManager
import com.example.alexeyglushkov.taskmanager.task.Tasks.TaskListener
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable
import java.lang.ref.WeakReference
import java.net.URL

class RssStorage : Parcelable, Serializable, TaskListener {
    companion object {
        private const val serialVersionUID = -4315556649639854776L

        @JvmField
        val CREATOR: Creator<RssStorage> = object : Creator<RssStorage> {
            override fun createFromParcel(`in`: Parcel): RssStorage {
                return RssStorage(`in`)
            }

            override fun newArray(size: Int): Array<RssStorage?> {
                return arrayOfNulls(size)
            }
        }
    }

    interface RssStorageCallback {
        fun completed(storage: RssStorage?, error: Error?)
    }

    interface RssFeedCallback {
        fun completed(feed: RssFeed?, error: Error?)
    }

    internal var loadStatus: Status? = null
    internal var loadError: Error? = null
    internal var keepStatus: Status? = null
    internal var keepError: Error? = null

    // Serializable
    var fileName: String? = null
        internal set
    var feeds = mutableListOf<RssFeed>()
    internal var context: WeakReference<Context>? = null
    protected var processingTask: Task? = null

    constructor(fileName: String?) {
        feeds = ArrayList()
        this.fileName = fileName
    }

    fun load(taskManager: TaskManager, context: Context?, callback: RssStorageCallback?) {
        val storage = this
        val fileLoadTask = FileLoadTask(fileName!!, ObjectReader<Any?>(null), context!!)
        fileLoadTask.taskCallback = object : Callback {
            override fun onCompleted(cancelled: Boolean) {
                val loadedStorage = fileLoadTask.taskResult as RssStorage?
                if (loadedStorage != null) {
                    feeds.addAll(loadedStorage.feeds)
                }
                callback?.completed(fileLoadTask.taskResult as RssStorage?, fileLoadTask.taskError)
            }
        }
        taskManager.addTask(fileLoadTask)
    }

    fun keep(taskManager: TaskManager, context: Context?, callback: RssStorageCallback?) {
        val storage = this
        val keepTask: FileKeepTask<*> = FileKeepTask(fileName!!, ObjectWriter<Any?>(null), storage, context!!)
        keepTask.taskCallback = object : Callback {
            override fun onCompleted(cancelled: Boolean) {
                callback?.completed(storage, keepTask.taskError)
            }
        }
        taskManager.addTask(keepTask)
    }

    fun addFeed(feed: RssFeed) {
        feeds.add(feed)
    }

    fun deleteFeed(feed: RssFeed?) {
        feeds.remove(feed)
    }

    fun removeFeed(feed: RssFeed?) {
        feeds.remove(feed)
    }

    fun getFeed(url: URL): RssFeed? {
        var result: RssFeed? = null
        for (feed in feeds) {
            if (feed.getURL() == url) {
                result = feed
                break
            }
        }
        return result
    }

    fun loadFeed(taskManager: TaskManager, context: Context?, feed: RssFeed, callback: RssFeedCallback?) {
        val streamReader: InputStreamDataReader<*> = ByteArrayReader(feed.dataHandler)
        val reader: HTTPConnectionStreamReader<*> = HTTPConnectionStreamReaderAdaptor(streamReader)
        val transport: HttpTaskTransport<*> = HttpTaskTransport(feed, reader)
        val loatTask = TransportTask(transport)
        loatTask.taskCallback = object : Callback {
            override fun onCompleted(cancelled: Boolean) {
                callback?.completed(feed, loatTask.taskError)
            }
        }
        taskManager.addTask(loatTask)
    }

    // TextFileLoadable
    fun loadStatus(): Status? {
        return loadStatus
    }

    fun setLoadStatus(st: Status?) {
        loadStatus = st
    }

    override fun setTaskInProgress(task: Task) {
        setLoadStatus(Status.Started)
        processingTask = task
    }

    override fun setTaskCompleted(task: Task) {
        if (processingTask === task) {
            setLoadStatus(Status.Finished)
            processingTask = null
        }
    }

    fun keepStatus(): Status? {
        return keepStatus
    }

    fun setKeepStatus(st: Status?) {
        keepStatus = st
    }

    //Parcelable
    override fun describeContents(): Int { // TODO Auto-generated method stub
        return 0
    }

    override fun writeToParcel(out: Parcel, flags: Int) {
        out.writeTypedList(feeds)
    }

    private constructor(`in`: Parcel) {
        `in`.readTypedList(feeds, RssFeed.Companion.CREATOR)
    }

    fun context(): Context? {
        return context!!.get()
    }

    @Throws(IOException::class)
    private fun writeObject(out: ObjectOutputStream) {
        out.writeInt(feeds.size)
        for (feed in feeds) {
            out.writeObject(feed)
        }
    }

    @Throws(IOException::class, ClassNotFoundException::class)
    private fun readObject(`in`: ObjectInputStream) { // populate the fields of 'this' from the data in 'in'...
        feeds = ArrayList()
        val size = `in`.readInt()
        for (i in 0 until size) {
            val feed = `in`.readObject() as RssFeed
            feeds.add(feed)
        }
    }
}