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
open class Image : Serializable, TaskListener, HttpURLConnectionProvider {
    private var _url: URL? = null
    override var url: URL
        get() = _url!!
        set(value) {_url = value }

    var width = 0
    var height = 0
    var byteSize = 0
    protected var loadStatus: Status? = null
    var loadPolicy = LoadPolicy.SkipIfAlreadyAdded
    protected var processingTask: WeakReference<Task>? = null

    override fun setTaskInProgress(task: Task) {
        Log.d("Image--", "start $task")
        processingTask = WeakReference(task)
        loadStatus = Status.Started
    }

    override fun setTaskCompleted(task: Task) {
        if (processingTask != null && task === processingTask!!.get()) {
            loadStatus = Status.Finished
            processingTask = null
        }
        Log.d("Image--", "completed $task")
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

    companion object {
        private const val serialVersionUID = 2567033384508404225L
    }
}