package com.ga.rss;

import java.io.IOException;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import org.apache.http.util.ByteArrayBuffer;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.ga.keeper.data.DataProvider;
import com.ga.keeper.file.ObjectKeepTask;
import com.ga.loader.data.DataHandler;
import com.ga.task.DataFormat;
import com.ga.loader.file.FileLoadTask;
import com.ga.loader.http.HttpLoadTask;
import com.ga.task.Task;
import com.ga.task.TaskManager;
import com.ga.task.Tasks;

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
        final FileLoadTask httpLoadTask = new FileLoadTask(getFileName(), getDataHandler(), context);

        taskManager.put(httpLoadTask, new Task.Callback() {
            @Override
            public void finished() {
                if (callback != null) {
                    callback.completed(storage, httpLoadTask.getTaskError());
                }
            }
        });
    }

    public void keep(TaskManager taskManager, Context context, final RssStorageCallback callback) {
        final RssStorage storage = this;
        final ObjectKeepTask keepTask = new ObjectKeepTask(getFileName(), getDataProvider(), context);

        taskManager.put(keepTask, new Task.Callback() {
            @Override
            public void finished() {
                if (callback != null) {
                    callback.completed(storage, keepTask.getTaskError());
                }
            }
        });
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

    public void loadFeed(TaskManager taskManager, Context context, final RssFeed feed, final RssFeedCallback callback) {

        final HttpLoadTask loatTask = new HttpLoadTask(feed.getUrlConnection(), feed.getDataHandler());

        taskManager.put(loatTask, new Task.Callback() {
            @Override
            public void finished() {
                if (callback != null) {
                    callback.completed(feed, loatTask.getTaskError());
                }
            }
        });
    }

    // TextFileLoadable

    public Task.Status loadStatus() {
        return loadStatus;
    }

    public void setLoadStatus(Task.Status st) {
        loadStatus = st;
    }

    public DataHandler getDataHandler() {
        return new DataHandler() {
            @Override
            public Error handleData(ByteArrayBuffer data) {
                return loadData(data);
            }
        };
    }

    private Error loadData(ByteArrayBuffer data) {
        /*TODO:
		if (obj instanceof RssStorage) {
			RssStorage storage = (RssStorage)obj;
			this.feeds = storage.feeds;
		}*/

        return null;
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

    // TextFileKeepable

    public DataProvider getDataProvider() {
        return new DataProvider() {
            @Override
            public ByteArrayBuffer getData() {
                return RssStorage.this.getData();
            }
        };
    }

    private ByteArrayBuffer getData() {
        //TODO:
        return null;
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
