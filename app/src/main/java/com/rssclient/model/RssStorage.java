package com.rssclient.model;

import java.io.IOException;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.alexeyglushkov.streamlib.data_readers_and_writers.ByteArrayReader;
import com.example.alexeyglushkov.streamlib.data_readers_and_writers.InputStreamDataReader;
import com.example.alexeyglushkov.streamlib.data_readers_and_writers.ObjectReader;
import com.example.alexeyglushkov.streamlib.data_readers_and_writers.ObjectWriter;
import com.example.alexeyglushkov.taskmanager.file.FileKeepTask;
import com.example.alexeyglushkov.taskmanager.file.FileLoadTask;
import com.example.alexeyglushkov.taskmanager.loader.http.HTTPConnectionStreamReader;
import com.example.alexeyglushkov.taskmanager.loader.http.HTTPConnectionStreamReaderAdaptor;
import com.example.alexeyglushkov.taskmanager.loader.http.TransportTask;
import com.example.alexeyglushkov.taskmanager.loader.http.HttpTaskTransport;
import com.example.alexeyglushkov.taskmanager.task.Task;
import com.example.alexeyglushkov.taskmanager.task.TaskManager;
import com.example.alexeyglushkov.taskmanager.task.Tasks;

public class RssStorage implements Parcelable, Serializable, Tasks.TaskListener {

    public interface RssStorageCallback {
        void completed(RssStorage storage, Error error);
    }

    public interface RssFeedCallback {
        void completed(RssFeed feed, Error error);
    }

    Task.Status loadStatus;
    Error loadError;

    Task.Status keepStatus;
    Error keepError;

    String fileName;
    ArrayList<RssFeed> feeds;

    WeakReference<Context> context;
    protected Task processingTask;

    public RssStorage(String fileName) {
        this.feeds = new ArrayList<RssFeed>();
        this.fileName = fileName;
    }

    public void load(TaskManager taskManager, Context context, final RssStorageCallback callback) {
        final RssStorage storage = this;
        final FileLoadTask fileLoadTask = new FileLoadTask(getFileName(), new ObjectReader(null), context);
        fileLoadTask.setTaskCallback(new Task.Callback() {
            @Override
            public void onCompleted(boolean cancelled) {
                RssStorage loadedStorage = (RssStorage)fileLoadTask.getTaskResult();
                if (loadedStorage != null) {
                    feeds.addAll(loadedStorage.feeds());
                }

                if (callback != null) {
                    callback.completed((RssStorage) fileLoadTask.getTaskResult(), fileLoadTask.getTaskError());
                }
            }
        });

        taskManager.addTask(fileLoadTask);
    }

    public void keep(TaskManager taskManager, Context context, final RssStorageCallback callback) {
        final RssStorage storage = this;
        final FileKeepTask keepTask = new FileKeepTask(getFileName(), new ObjectWriter(null), storage, context);
        keepTask.setTaskCallback(new Task.Callback() {
            @Override
            public void onCompleted(boolean cancelled) {
                if (callback != null) {
                    callback.completed(storage, keepTask.getTaskError());
                }
            }
        });

        taskManager.addTask(keepTask);
    }

    public void addFeed(RssFeed feed) {
        feeds.add(feed);
    }

    public void deleteFeed(RssFeed feed) {
        feeds.remove(feed);
    }

    public void removeFeed(RssFeed feed) {
        feeds.remove(feed);
    }

    public ArrayList<RssFeed> feeds() {
        return feeds;
    }

    public RssFeed getFeed(URL url) {
        RssFeed result = null;
        for (RssFeed feed : feeds) {
            if (feed.getURL().equals(url)) {
                result = feed;
                break;
            }
        }

        return result;
    }

    public void loadFeed(TaskManager taskManager, Context context, final RssFeed feed, final RssFeedCallback callback) {

        InputStreamDataReader streamReader = new ByteArrayReader(feed.getDataHandler());
        HTTPConnectionStreamReader reader = new HTTPConnectionStreamReaderAdaptor(streamReader);
        HttpTaskTransport transport = new HttpTaskTransport(feed, reader);

        final TransportTask loatTask = new TransportTask(transport);
        loatTask.setTaskCallback(new Task.Callback() {
            @Override
            public void onCompleted(boolean cancelled) {
                if (callback != null) {
                    callback.completed(feed, loatTask.getTaskError());
                }
            }
        });

        taskManager.addTask(loatTask);
    }

    // TextFileLoadable

    public Task.Status loadStatus() {
        return loadStatus;
    }

    public void setLoadStatus(Task.Status st) {
        loadStatus = st;
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

    public Task.Status keepStatus() {
        return keepStatus;
    }

    public void setKeepStatus(Task.Status st) {
        keepStatus = st;
    }

    //Parcelable

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeTypedList(feeds);
    }

    public static final Parcelable.Creator<RssStorage> CREATOR
            = new Parcelable.Creator<RssStorage>() {
        public RssStorage createFromParcel(Parcel in) {
            return new RssStorage(in);
        }

        public RssStorage[] newArray(int size) {
            return new RssStorage[size];
        }
    };

    private RssStorage(Parcel in) {
        in.readTypedList(feeds, RssFeed.CREATOR);
    }

    // Serializable

    public String getFileName() {
        return fileName;
    }

    public Context context() {
        return context.get();
    }

    private static final long serialVersionUID = 0L;

    private void writeObject(java.io.ObjectOutputStream out)
            throws IOException {
        out.writeInt(feeds.size());
        for (RssFeed feed : feeds) {
            out.writeObject(feed);
        }
    }

    private void readObject(java.io.ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        // populate the fields of 'this' from the data in 'in'...

        feeds = new ArrayList<RssFeed>();

        int size = in.readInt();
        for (int i = 0; i < size; ++i) {
            RssFeed feed = (RssFeed) in.readObject();
            feeds.add(feed);
        }
    }
}
