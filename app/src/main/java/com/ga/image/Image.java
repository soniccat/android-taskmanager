package com.ga.image;

import java.io.IOException;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.util.ByteArrayBuffer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
//import android.media.Image;

import com.ga.loader.data.ByteArrayBufferHandler;
import com.ga.task.Task;
import com.ga.task.Tasks;

//TODO: think about implementing task container and moving check of setTaskCompleted to taskmanager
public class Image implements Serializable, Tasks.TaskListener {
    protected URL url;
    protected int width;
    protected int height;
    protected int byteSize;
    protected Task.Status loadStatus;
    protected Task.LoadPolicy loadPolicy = Task.LoadPolicy.SkipIfAdded;
    protected Task processingTask;

    @Override
    public void setTaskInProgress(Task task) {
        processingTask = task;
        loadStatus = Task.Status.Started;
    }

    @Override
    public void setTaskCompleted(Task task) {
        if (task == processingTask) {
            loadStatus = Task.Status.Finished;
            processingTask = null;
        }
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public URL url() {
        return this.url;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int width() {
        return this.width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int height() {
        return height;
    }

    public int getByteSize() {
        return byteSize;
    }

    public void setByteSize(int byteSize) {
        this.byteSize = byteSize;
    }

    public Task.LoadPolicy getLoadPolicy() {
        return loadPolicy;
    }

    public void setLoadPolicy(Task.LoadPolicy loadPolicy) {
        this.loadPolicy = loadPolicy;
    }

    private static final long serialVersionUID = 0L;

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        // write 'this' to 'out'...
        out.writeUTF(url.toString());
        out.writeInt(width);
        out.writeInt(height);
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException,
            ClassNotFoundException {
        // populate the fields of 'this' from the data in 'in'...

        String urlString = in.readUTF();

        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        width = in.readInt();
        height = in.readInt();
    }

    public Task.Status loadStatus() {
        return loadStatus;
    }

    public HttpURLConnection getUrlConnection() {

        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection) this.url.openConnection();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return connection;
    }

    public ByteArrayBufferHandler getDataHandler() {
        return new ByteArrayBufferHandler() {
            @Override
            public Error handleByteArrayBuffer(ByteArrayBuffer byteArray) {
                return loadData(byteArray);
            }
        };
    }

    private Error loadData(ByteArrayBuffer data) {
        //suppose that data will be handled later
        return null;
    }

    public static Bitmap bitmapFromByteArray(ByteArrayBuffer data) {
        byte[] imageData = data.toByteArray();
        return BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
    }
}
