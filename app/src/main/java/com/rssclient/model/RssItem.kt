package com.rssclient.model

import com.aglushkov.taskmanager_http.image.Image
import org.xmlpull.v1.XmlSerializer
import java.io.IOException

class RssItem {
    internal var title: String? = null
    internal var link: String? = null
    internal var description: String? = null
    internal var image: Image? = null
    fun title(): String? {
        return title
    }

    fun setTitle(title: String?) {
        this.title = title
    }

    fun link(): String? {
        return link
    }

    fun setLink(link: String?) {
        this.link = link
    }

    fun description(): String? {
        return description
    }

    fun setDescription(description: String?) {
        this.description = description
    }

    fun setImage(image: Image?) {
        this.image = image
    }

    fun image(): Image? {
        return image
    }

    @Throws(IllegalArgumentException::class, IllegalStateException::class, IOException::class)
    fun fillXmlElement(serializer: XmlSerializer) {
        serializer.startTag("", "item")
        //serializer.attribute(namespace, name, value)
        serializer.endTag("", "item")
    }
}