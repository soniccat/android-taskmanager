package com.rssclient.model

import android.os.Parcelable
import com.aglushkov.taskmanager_http.image.Image
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RssItem(
    var title: String,
    var link: String,
    var description: String? = null,
    var image: Image? = null): Parcelable