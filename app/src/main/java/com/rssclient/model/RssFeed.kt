package com.rssclient.model

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import android.util.Xml
import com.aglushkov.taskmanager_http.image.Image
import com.aglushkov.taskmanager_http.loader.http.HttpURLConnectionProvider
import com.example.alexeyglushkov.streamlib.handlers.ByteArrayHandler
import com.example.alexeyglushkov.taskmanager.task.Task
import com.example.alexeyglushkov.taskmanager.task.Task.Status
import com.example.alexeyglushkov.taskmanager.task.Tasks.TaskListener
import kotlinx.android.parcel.Parcelize
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.nio.charset.Charset

@Parcelize
data class RssFeed(var name: String,
                   var url: URL,
                   var image: Image?,
                   var items: List<RssItem> = ArrayList()) : Parcelable/*, TaskListener, HttpURLConnectionProvider*/ {
//    companion object {
//        private const val serialVersionUID = 7603336069987943527L
//
//        @JvmField
//        val CREATOR: Creator<RssFeed> = object : Creator<RssFeed> {
//            override fun createFromParcel(`in`: Parcel): RssFeed {
//                return RssFeed(`in`)
//            }
//
//            override fun newArray(size: Int): Array<RssFeed?> {
//                return arrayOfNulls(size)
//            }
//        }
//    }

//    protected var processingTask: Task? = null


    fun loadStatus(): Status {
        return loadStatus
    }

    fun setLoadStatus(st: Status) {
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

    override fun getUrlConnection(): HttpURLConnection {
        var connection: HttpURLConnection? = null
        try {
            connection = url!!.openConnection() as HttpURLConnection
        } catch (e: Exception) { // TODO Auto-generated catch block
            e.printStackTrace()
        }
        return connection!!
    }

    override fun getURL(): URL {
        return url!!
    }

    val dataHandler: ByteArrayHandler<*>
        get() = object : ByteArrayHandler<Any?> {
            override fun convert(`object`: ByteArray?): Any? {
                return loadData(`object` as ByteArray)
            }
        }

    private fun loadData(data: ByteArray): Error { // TODO Auto-generated method stub
        println("ready to parse")
        val ch = Charset.forName("UTF-8")
        val str = String(data, ch)
        println(str)
        var reader: StringReader? = null
        return try {
            val parser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            reader = StringReader(str)
            parser.setInput(reader)
            parser.nextTag()
            readChannels(parser)!!
        } catch (ex: Exception) {
            Error("Rss parse error")
        } finally {
            reader!!.close()
        }
    }

    internal fun readChannels(parser: XmlPullParser): Error? {
        val name: String?
        try {
            parser.nextTag()
        } catch (e: Exception) {
            return Error("Rss channel parse error")
        }
        name = parser.name
        return if (name != null && parser.name == "channel") {
            readHead(parser)
        } else null
    }

    internal fun readHead(parser: XmlPullParser): Error? {
        items = ArrayList()
        return try {
            while (parser.next() != XmlPullParser.END_TAG) {
                val name = parser.name
                var startedTag = parser.eventType == XmlPullParser.START_TAG
                if (name != null) {
                    if (name == "image") {
                        image = readImage(parser)
                    } else if (name == "item") {
                        val item = readItem(parser)
                        if (item != null) {
                            items.add(item)
                        }
                    }
                }
                if (startedTag) {
                    skip(parser)
                    startedTag = false
                }
            }
            null
        } catch (e: Exception) {
            e.printStackTrace()
            Error("Rss head parse error")
        }
    }

    internal fun readItem(parser: XmlPullParser): RssItem? {
        var title: String? = null
        var link: String? = null
        var description: String? = null
        var image: Image? = null

        try {
            while (parser.next() != XmlPullParser.END_TAG) {
                var startedTag = parser.eventType == XmlPullParser.START_TAG
                val name = parser.name
                if (name != null) {
                    if (name == "title") {
                        parser.next()
                        title = parser.text
                    } else if (name == "description") {
                        parser.next()
                        description = parser.text
                    } else if (name == "link") {
                        parser.next()
                        link = parser.text
                    } else {
                        if (image == null) {
                            image = tryToFindImageInAttributes(parser)
                        }
                    }
                }
                if (startedTag) {
                    skip(parser)
                    startedTag = false
                }
            }
        } catch (e: Exception) {
            return null
        }

        return if (title != null && link != null) {
            RssItem(title, link, description, image)
        } else {
            null
        }
    }

    internal fun tryToFindImageInAttributes(parser: XmlPullParser): Image? {
        var image: Image? = null
        var byteSize = 0
        val attrCount = parser.attributeCount
        for (i in 0 until attrCount) {
            val str = parser.getAttributeValue(i)
            if (stringIsImageUrl(str)) {
                var url: URL? = null
                try {
                    url = URL(str)
                    image = Image()
                    image.setUrl(url)
                } catch (e: MalformedURLException) {
                }
            }
            if (parser.getAttributeName(i) == "length") {
                byteSize = parser.getAttributeValue(i).toInt()
            }
        }
        if (image != null) {
            image.byteSize = byteSize
        }
        return image
    }

    internal fun stringIsImageUrl(string: String): Boolean {
        return if (string.endsWith(".png") || string.endsWith(".jpg")) {
            true
        } else false
    }

    internal fun readImage(parser: XmlPullParser): Image? {
        val item = Image()
        try {
            while (parser.next() != XmlPullParser.END_TAG) {
                var startedTag = parser.eventType == XmlPullParser.START_TAG
                val name = parser.name
                if (name != null) {
                    if (name == "url") {
                        parser.next()
                        try {
                            val url = URL(parser.text)
                            item.setUrl(url)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else if (name == "length") {
                        item.byteSize = parser.text.toInt()
                    } else if (name == "width") {
                        parser.next()
                        item.setWidth(parser.text.toInt())
                    } else if (name == "height") {
                        parser.next()
                        item.setHeight(parser.text.toInt())
                    }
                }
                if (startedTag) {
                    skip(parser)
                    startedTag = false
                }
            }
        } catch (e: Exception) {
            return null
        }
        return item
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun skip(parser: XmlPullParser) {
        if (parser.eventType == XmlPullParser.END_TAG) {
            parser.next()
            return
        }
        var depth = 1
        while (depth != 0) {
            when (parser.next()) {
                XmlPullParser.END_TAG -> depth--
                XmlPullParser.START_TAG -> depth++
            }
        }
    }

    // Parcelable
    override fun describeContents(): Int { // TODO Auto-generated method stub
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(name)
        dest.writeString(url.toString())
    }

    private constructor(`in`: Parcel) {
        name = `in`.readString()
        val urlString = `in`.readString()
        try {
            url = URL(urlString)
        } catch (e: MalformedURLException) { // TODO Auto-generated catch block
            e.printStackTrace()
        }
    }

    // Serializable
    @Throws(IOException::class)
    private fun writeObject(out: ObjectOutputStream) { // write 'this' to 'out'...
        out.writeUTF(name)
        out.writeUTF(url.toString())
        out.writeObject(image)
    }

    @Throws(IOException::class, ClassNotFoundException::class)
    private fun readObject(`in`: ObjectInputStream) { // populate the fields of 'this' from the data in 'in'...
        name = `in`.readUTF()
        val urlString = `in`.readUTF()
        try {
            url = URL(urlString)
        } catch (e: MalformedURLException) { // TODO Auto-generated catch block
            e.printStackTrace()
        }
        image = `in`.readObject() as Image

        if (items == null) {
            items = ArrayList<RssItem>()
        }
    }
}