package com.aglushkov.taskmanager_http.image

import com.aglushkov.taskmanager_http.loader.http.HttpURLConnectionProvider
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable
import java.net.HttpURLConnection
import java.net.URL

open class Image : Serializable, HttpURLConnectionProvider {
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

    @Throws(IOException::class)
    private fun writeObject(out: ObjectOutputStream) {
        out.writeUTF(url.toString())
        out.writeInt(width)
        out.writeInt(height)
    }

    @Throws(IOException::class, ClassNotFoundException::class)
    private fun readObject(`in`: ObjectInputStream) {
        val urlString = `in`.readUTF()
        url = URL(urlString)
        width = `in`.readInt()
        height = `in`.readInt()
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

    override fun getUrlConnection(): HttpURLConnection {
        return url.openConnection() as HttpURLConnection
    }
}