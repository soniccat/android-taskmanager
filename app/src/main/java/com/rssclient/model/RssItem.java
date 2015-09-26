package com.rssclient.model;

import java.io.IOException;

import org.xmlpull.v1.XmlSerializer;

import com.taskmanager.image.Image;

public class RssItem {
    String title;
    String link;
    String description;
    Image image;

    public String title() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String link() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String description() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public Image image() {
        return this.image;
    }

    public void fillXmlElement(XmlSerializer serializer) throws IllegalArgumentException, IllegalStateException, IOException {
        serializer.startTag("", "item");
        //serializer.attribute(namespace, name, value)
        serializer.endTag("", "item");
    }
}
