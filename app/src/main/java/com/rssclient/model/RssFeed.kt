package com.rssclient.model

import android.os.Parcelable
import com.aglushkov.taskmanager_http.image.Image
import kotlinx.android.parcel.Parcelize
import java.net.URL

@Parcelize
data class RssFeed(var name: String,
                   var url: URL?,
                   var image: Image? = null,
                   var items: List<RssItem> = ArrayList()) : Parcelable/*, TaskListener, HttpURLConnectionProvider*/ {
//    protected var processingTask: Task? = null
//
//    fun loadStatus(): Status {
//        return loadStatus
//    }
//
//    fun setLoadStatus(st: Status) {
//        loadStatus = st
//    }
//
//    override fun setTaskInProgress(task: Task) {
//        setLoadStatus(Status.Started)
//        processingTask = task
//    }
//
//    override fun setTaskCompleted(task: Task) {
//        if (processingTask === task) {
//            setLoadStatus(Status.Finished)
//            processingTask = null
//        }
//    }
//
//    override fun getUrlConnection(): HttpURLConnection {
//        var connection: HttpURLConnection? = null
//        try {
//            connection = url.openConnection() as HttpURLConnection
//        } catch (e: Exception) { // TODO Auto-generated catch block
//            e.printStackTrace()
//        }
//        return connection!!
//    }

//    override fun getURL(): URL {
//        return url!!
//    }

//    // Parcelable
//    override fun describeContents(): Int { // TODO Auto-generated method stub
//        return 0
//    }
//
//    override fun writeToParcel(dest: Parcel, flags: Int) {
//        dest.writeString(name)
//        dest.writeString(url.toString())
//    }
//
//    private constructor(`in`: Parcel) {
//        name = `in`.readString()
//        val urlString = `in`.readString()
//        try {
//            url = URL(urlString)
//        } catch (e: MalformedURLException) { // TODO Auto-generated catch block
//            e.printStackTrace()
//        }
//    }

//    // Serializable
//    @Throws(IOException::class)
//    private fun writeObject(out: ObjectOutputStream) { // write 'this' to 'out'...
//        out.writeUTF(name)
//        out.writeUTF(url.toString())
//        out.writeObject(image)
//    }
//
//    @Throws(IOException::class, ClassNotFoundException::class)
//    private fun readObject(`in`: ObjectInputStream) { // populate the fields of 'this' from the data in 'in'...
//        name = `in`.readUTF()
//        val urlString = `in`.readUTF()
//        try {
//            url = URL(urlString)
//        } catch (e: MalformedURLException) { // TODO Auto-generated catch block
//            e.printStackTrace()
//        }
//        image = `in`.readObject() as Image
//
//        if (items == null) {
//            items = ArrayList<RssItem>()
//        }
//    }
}