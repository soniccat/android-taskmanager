package com.rssclient.model;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import org.apache.http.util.ByteArrayBuffer;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Xml;

import com.example.alexeyglushkov.streamlib.handlers.ByteArrayHandler;
import com.example.alexeyglushkov.taskmanager.image.Image;
import com.example.alexeyglushkov.taskmanager.loader.http.HttpURLConnectionProvider;
import com.example.alexeyglushkov.taskmanager.task.Task;
import com.example.alexeyglushkov.taskmanager.task.Tasks;

public class RssFeed implements Parcelable, Serializable, Tasks.TaskListener, HttpURLConnectionProvider {
    String name;
    URL url;
    Task.Status loadStatus = Task.Status.NotStarted;
    Image image;
    ArrayList<RssItem> items;
    protected Task processingTask;

    public RssFeed() {
        this.items = new ArrayList<RssItem>();
    }

    public RssFeed(URL url, String name) {
        this();
        this.setName(name);
        this.setUrl(url);
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public URL url() {
        return this.url;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String name() {
        return name;
    }

    public ArrayList<RssItem> items() {
        return items;
    }

    public Image image() {
        return image;
    }

    public Task.Status loadStatus() {
        return loadStatus;
    }

    public void setLoadStatus(Task.Status st) {
        this.loadStatus = st;
    }

    @Override
    public void setTaskInProgress(Task task) {
        setLoadStatus(Task.Status.Started);
        processingTask = task;
    }

    @Override
    public void setTaskCompleted(Task task) {
        if (processingTask == task) {
            setLoadStatus(Task.Status.Finished);
            processingTask = null;
        }
    }

    public HttpURLConnection getUrlConnection() {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) this.url.openConnection();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return connection;
    }

    @Override
    public URL getURL() {
        return this.url;
    }

    public ByteArrayHandler getDataHandler() {
        return new ByteArrayHandler() {
            @Override
            public Object handleByteArrayBuffer(ByteArrayBuffer byteArray) {
                return loadData(byteArray);
            }

            @Override
            public Object convert(Object object) {
                return handleByteArrayBuffer((ByteArrayBuffer)object);
            }
        };
    }

    private Error loadData(ByteArrayBuffer data) {
        // TODO Auto-generated method stub
        System.out.println("ready to parse");

        Charset ch = Charset.forName("UTF-8");
        String str = new String(data.buffer(), ch);
        System.out.println(str);

        StringReader reader = null;

        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);

            reader = new StringReader(str);
            parser.setInput(reader);

            parser.nextTag();
            return readChannels(parser);

        } catch (Exception ex) {
            return new Error("Rss parse error");

        } finally {
            reader.close();
        }
    }

    Error readChannels(XmlPullParser parser) {
        String name;

        try {
            parser.nextTag();
        } catch (Exception e) {
            return new Error("Rss channel parse error");
        }

        name = parser.getName();
        if (name != null && parser.getName().equals("channel")) {
            return this.readHead(parser);
        }

        return null;
    }

    Error readHead(XmlPullParser parser) {
        this.items = new ArrayList<RssItem>();

        try {
            while (parser.next() != XmlPullParser.END_TAG) {
                String name = parser.getName();
                boolean startedTag = parser.getEventType() == XmlPullParser.START_TAG;

                if (name != null) {
                    if (name.equals("image")) {
                        this.image = this.readImage(parser);

                    } else if (name.equals("item")) {
                        RssItem item = this.readItem(parser);
                        if (item != null) {
                            this.items.add(item);
                        }
                    }
                }

                if (startedTag) {
                    skip(parser);
                    startedTag = false;
                }
            }

            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return new Error("Rss head parse error");
        }
    }

    RssItem readItem(XmlPullParser parser) {
        RssItem item = new RssItem();

        try {
            while (parser.next() != XmlPullParser.END_TAG) {
                boolean startedTag = parser.getEventType() == XmlPullParser.START_TAG;
                String name = parser.getName();

                if (name != null) {
                    if (name.equals("title")) {
                        parser.next();
                        item.setTitle(parser.getText());

                    } else if (name.equals("description")) {
                        parser.next();
                        item.setDescription(parser.getText());

                    } else if (name.equals("link")) {
                        parser.next();
                        item.setLink(parser.getText());

                    } else {
                        if (item.image() == null) {
                            Image image = tryToFindImageInAttributes(parser);
                            if (image != null) {
                                item.setImage(image);
                            }
                        }
                    }
                }

                if (startedTag) {
                    skip(parser);
                    startedTag = false;
                }
            }

        } catch (Exception e) {
            return null;
        }

        return item;
    }

    Image tryToFindImageInAttributes(XmlPullParser parser) {
        Image image = null;
        int byteSize = 0;

        int attrCount = parser.getAttributeCount();
        for (int i = 0; i < attrCount; ++i) {
            String str = parser.getAttributeValue(i);
            if (stringIsImageUrl(str)) {
                URL url = null;
                try {
                    url = new URL(str);
                    image = new Image();
                    image.setUrl(url);
                } catch (MalformedURLException e) {

                }
            }

            if (parser.getAttributeName(i).equals("length")) {
                byteSize = Integer.parseInt(parser.getAttributeValue(i));
            }
        }

        if (image != null) {
            image.setByteSize(byteSize);
        }

        return image;
    }

    boolean stringIsImageUrl(String string) {
        if (string.endsWith(".png") || string.endsWith(".jpg")) {
            return true;
        }

        return false;
    }

    Image readImage(XmlPullParser parser) {
        Image item = new Image();

        try {
            while (parser.next() != XmlPullParser.END_TAG) {
                boolean startedTag = parser.getEventType() == XmlPullParser.START_TAG;
                String name = parser.getName();

                if (name != null) {
                    if (name.equals("url")) {
                        parser.next();

                        try {
                            URL url = new URL(parser.getText());
                            item.setUrl(url);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else if (name.equals("length")) {
                        item.setByteSize(Integer.parseInt(parser.getText()));

                    } else if (name.equals("width")) {
                        parser.next();
                        item.setWidth(Integer.parseInt(parser.getText()));

                    } else if (name.equals("height")) {
                        parser.next();
                        item.setHeight(Integer.parseInt(parser.getText()));
                    }
                }

                if (startedTag) {
                    skip(parser);
                    startedTag = false;
                }
            }

        } catch (Exception e) {
            return null;
        }

        return item;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() == XmlPullParser.END_TAG) {
            parser.next();
            return;
        }

        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

    // Parcelable

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(url.toString());
    }

    private RssFeed(Parcel in) {
        name = in.readString();

        String urlString = in.readString();

        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static final Parcelable.Creator<RssFeed> CREATOR = new Parcelable.Creator<RssFeed>() {

        public RssFeed createFromParcel(Parcel in) {
            return new RssFeed(in);
        }

        public RssFeed[] newArray(int size) {
            return new RssFeed[size];
        }
    };

    // Serializable

    private static final long serialVersionUID = 0L;

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        // write 'this' to 'out'...
        out.writeUTF(name);
        out.writeUTF(url.toString());
        out.writeObject(image);
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException,
            ClassNotFoundException {
        // populate the fields of 'this' from the data in 'in'...
        name = in.readUTF();

        String urlString = in.readUTF();

        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        image = (Image) in.readObject();
    }
}
