package com.rssclient.model

import com.aglushkov.taskmanager_http.image.Image

data class RssItem(
    var title: String,
    var link: String,
    var description: String? = null,
    var image: Image? = null)