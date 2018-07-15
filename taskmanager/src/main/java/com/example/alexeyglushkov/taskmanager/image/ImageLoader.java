package com.example.alexeyglushkov.taskmanager.image;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.example.alexeyglushkov.streamlib.convertors.BytesBitmapConverter;
import com.example.alexeyglushkov.streamlib.data_readers_and_writers.ByteArrayReader;
import com.example.alexeyglushkov.streamlib.data_readers_and_writers.InputStreamDataReader;
import com.example.alexeyglushkov.taskmanager.loader.http.HTTPConnectionStreamReader;
import com.example.alexeyglushkov.taskmanager.loader.http.HTTPConnectionStreamReaderAdaptor;
import com.example.alexeyglushkov.taskmanager.loader.http.TransportTask;
import com.example.alexeyglushkov.taskmanager.loader.http.HttpTaskTransport;
import com.example.alexeyglushkov.taskmanager.task.Task;
import com.example.alexeyglushkov.taskmanager.task.Tasks;
import com.example.alexeyglushkov.tools.HandlerTools;

import junit.framework.Assert;

// Because Image doesn't store loaded data we should use ImageLoader to get the data from callback

public class ImageLoader {

    //TODO: write argument descriptions
    public static Task loadImage(Handler handler, final Image image, final ImageLoader.LoadCallback callback) {
        return loadImage(handler, image, null, callback);
    }

    public static Task loadImage(final Handler handler, final Image image, String destinationId, final ImageLoader.LoadCallback callback) {
        InputStreamDataReader streamReader = new ByteArrayReader(new BytesBitmapConverter());
        HTTPConnectionStreamReader reader = new HTTPConnectionStreamReaderAdaptor(streamReader);
        final TransportTask transportTask = createTask(image, destinationId, reader);

        Task.Callback taskCallback = getTaskCallback(transportTask, image, callback);
        transportTask.setTaskCallback(taskCallback);

        HandlerTools.runOnHandlerThread(handler, new Runnable() {
            @Override
            public void run() {
                // to have addTaskStatusListener called on a handler's thread
                Tasks.bindOnTaskCompletion(transportTask, image);
            }
        });

        // TODO: need to check case when a task was refused by a tak provider
        // maybe it's ok to set waiting on a handler's thread, to be able to bind on that before adding
        // and handle it in bindOnTaskCompletion listener
        Assert.assertEquals(Looper.myLooper(), Looper.getMainLooper());
        image.setTaskInProgress(transportTask);

        return transportTask;
    }

    @NonNull
    private static TransportTask createTask(Image image, String destinationId, HTTPConnectionStreamReader reader) {
        HttpTaskTransport transport = new HttpTaskTransport(image, reader);
        transport.setContentLength(image.getByteSize());

        final TransportTask transportTask = new TransportTask(transport);
        transportTask.setLoadPolicy(image.getLoadPolicy());

        if (transportTask.getTaskId() != null && destinationId != null) {
            transportTask.setTaskId(transportTask.getTaskId() + destinationId);

        } else if (transportTask.getTaskId() != null) {
            transportTask.setTaskId(transportTask.getTaskId() + image.hashCode());
        }

        return transportTask;
    }

    @NonNull
    public static Task.Callback getTaskCallback(final TransportTask transportTask, final Image image, final LoadCallback callback) {
        return new Task.Callback() {
            @Override
            public void onCompleted(boolean cancelled) {
                //ignore a cancelled result
                if (callback != null && transportTask.getTaskStatus() == Task.Status.Finished) {
                    Bitmap bitmap = null;
                    if (transportTask.getHandledData() != null) {
                        bitmap = (Bitmap) transportTask.getHandledData();
                    }

                    if (image instanceof ImageWithData) {
                        ImageWithData imageWithData = (ImageWithData)image;
                        imageWithData.setBitmap(bitmap);
                    }

                    callback.completed(transportTask, image, bitmap, transportTask.getTaskError());
                }
            }
        };
    }

    public interface LoadCallback {
        void completed(Task task, Image image, Bitmap bitmap, Error error);
    }
}
