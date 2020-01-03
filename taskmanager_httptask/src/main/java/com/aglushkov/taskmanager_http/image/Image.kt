package com.aglushkov.taskmanager_http.image

import android.util.Log
import com.aglushkov.taskmanager_http.loader.http.HttpURLConnectionProvider
import com.example.alexeyglushkov.taskmanager.task.Task
import com.example.alexeyglushkov.taskmanager.task.Task.LoadPolicy
import com.example.alexeyglushkov.taskmanager.task.Task.Status
import com.example.alexeyglushkov.taskmanager.task.Tasks.TaskListener
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable
import java.lang.ref.WeakReference
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

//import android.media.Image;
//TODO: think about implementing task container and moving check of setTaskCompleted to taskmanager
//TODO: clean this class: remove writeObject/readObject, TaskListener
open class Image : Serializable, TaskListener, HttpURLConnectionProvider {
    companion object {
        private const val serialVersionUID = 2567033384508404225L
    }

    private var _url: URL? = null
    override var url: URL
        get() = _url!!
        set(value) {_url = value }

    var width = 0
    var height = 0
    var byteSize = 0

    var loadPolicy = LoadPolicy.CompleteWhenAlreadyAddedCompletes
    protected var loadStatus: Status? = null
    protected var processingTask: WeakReference<Task>? = null

    override fun setTaskInProgress(task: Task) {
        //Log.d("Image--", "start $task")
        processingTask = WeakReference(task)
        loadStatus = Status.Started
    }

    override fun setTaskCompleted(task: Task) {
        if (processingTask != null && task === processingTask!!.get()) {
            loadStatus = Status.Completed
            processingTask = null
        }
        //Log.d("Image--", "completed $task")
    }

    @Throws(IOException::class)
    private fun writeObject(out: ObjectOutputStream) { // write 'this' to 'out'...
        out.writeUTF(url.toString())
        out.writeInt(width)
        out.writeInt(height)
        out.writeInt(loadPolicy.ordinal)
    }

    @Throws(IOException::class, ClassNotFoundException::class)
    private fun readObject(`in`: ObjectInputStream) { // populate the fields of 'this' from the data in 'in'...
        val urlString = `in`.readUTF()
        try {
            url = URL(urlString)
        } catch (e: MalformedURLException) { // TODO Auto-generated catch block
            e.printStackTrace()
        }

        width = `in`.readInt()
        height = `in`.readInt()

        val loadPolicyInt = `in`.readInt()
        loadPolicy = LoadPolicy.values()[loadPolicyInt]
    }

    fun loadStatus(): Status? {
        return loadStatus
    }

    override fun getUrlConnection(): HttpURLConnection {
        return url.openConnection() as HttpURLConnection
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Image

        if (_url != other._url) return false

        return true
    }

    override fun hashCode(): Int {
        return _url?.hashCode() ?: 0
    }
}