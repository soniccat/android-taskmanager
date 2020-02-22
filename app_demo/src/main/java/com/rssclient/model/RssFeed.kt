package com.rssclient.model

import android.os.Parcelable
import com.aglushkov.taskmanager_http.image.Image
import kotlinx.android.parcel.Parcelize
import java.io.Serializable
import java.net.URL



@Parcelize
data class RssFeed(
        var name: String,
        var url: URL,
        var image: Image? = null,
        var items: List<RssItem> = ArrayList()) : Parcelable, Serializable {

    companion object {
        private const val serialVersionUID = 715000812082812655L
    }
}