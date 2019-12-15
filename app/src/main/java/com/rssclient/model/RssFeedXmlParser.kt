package com.rssclient.model

import android.util.Xml
import com.aglushkov.taskmanager_http.image.Image
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.StringReader
import java.net.MalformedURLException
import java.net.URL
import java.nio.charset.Charset

// TODO: rewrite this legacy
class RssFeedXmlParser {
    fun parse(data: ByteArray): RssFeed {
        val ch = Charset.forName("UTF-8")
        val str = String(data, ch)
        var reader: StringReader? = null

        return try {
            val parser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            reader = StringReader(str)
            parser.setInput(reader)
            parser.nextTag()
            parse(parser)!!
        } finally {
            reader!!.close()
        }
    }

    internal fun parse(parser: XmlPullParser): RssFeed? {
        val name: String?
        parser.nextTag()

        name = parser.name
        return if (name != null && parser.name == "channel") {
            try {
                val feed = RssFeed(name, null, null)
                this.parse(parser, feed)
                feed
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                throw e
            }
        } else
            null
    }

    internal fun parse(parser: XmlPullParser, feed: RssFeed) {
        val items = mutableListOf<RssItem>()
        while (parser.next() != XmlPullParser.END_TAG) {
            val name = parser.name
            var startedTag = parser.eventType == XmlPullParser.START_TAG
            if (name != null) {
                if (name == "image") {
                    feed.image = this.readImage(parser)
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

        feed.items = items
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
                    image.url = url
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
                            item.url = url
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else if (name == "length") {
                        item.byteSize = parser.text.toInt()
                    } else if (name == "width") {
                        parser.next()
                        item.width = parser.text.toInt()
                    } else if (name == "height") {
                        parser.next()
                        item.height = parser.text.toInt()
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
}