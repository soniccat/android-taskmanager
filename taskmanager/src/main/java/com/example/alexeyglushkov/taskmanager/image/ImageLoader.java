package com.example.alexeyglushkov.taskmanager.image;

import android.graphics.Bitmap;

import com.example.alexeyglushkov.streamlib.convertors.BytesBitmapConvertor;
import com.example.alexeyglushkov.streamlib.readersandwriters.ByteArrayReader;
import com.example.alexeyglushkov.streamlib.readersandwriters.InputStreamReader;
import com.example.alexeyglushkov.taskmanager.loader.http.HTTPConnectionStreamReader;
import com.example.alexeyglushkov.taskmanager.loader.http.HTTPConnectionResponseReaderAdaptor;
import com.example.alexeyglushkov.taskmanager.loader.http.HttpLoadTask;
import com.example.alexeyglushkov.taskmanager.task.Task;
import com.example.alexeyglushkov.taskmanager.task.TaskManager;
import com.example.alexeyglushkov.taskmanager.task.Tasks;

import junit.framework.Assert;

// Because Image doesn't store loaded data we should use ImageLoader to get the data from callback

public class ImageLoader {

    //TODO: write argument descriptions
    public static Task loadImage(TaskManager taskManager, final Image image, final ImageLoader.LoadCallback callback) {
        return loadImage(taskManager, image, null, callback);
    }

    public static Task loadImage(TaskManager taskManager, final Image image, String destinationId, final ImageLoader.LoadCallback callback) {
        InputStreamReader streamReader = new ByteArrayReader(new BytesBitmapConvertor(null));
        HTTPConnectionStreamReader reader = new HTTPConnectionResponseReaderAdaptor(streamReader);
        final HttpLoadTask httpLoadTask = new HttpLoadTask(image, reader);

        httpLoadTask.setLoadPolicy(image.getLoadPolicy());
        httpLoadTask.setContentLength(image.getByteSize());

        if (httpLoadTask.getTaskId() != null) {
            httpLoadTask.setTaskId(httpLoadTask.getTaskId() + image.hashCode());
        }

        Tasks.bindOnTaskCompletion(httpLoadTask, image);

        if (httpLoadTask.getTaskId() != null && destinationId != null) {
            httpLoadTask.setTaskId(httpLoadTask.getTaskId() + destinationId);
        }

        // TODO: it seems it's better to return callback in another function to be able to use it
        // also for restoring in RestorableTaskProvider
        httpLoadTask.setTaskCallback(new Task.Callback() {
            @Override
            public void onCompleted(boolean cancelled) {
                //ignore a cancelled result
                if (callback != null && httpLoadTask.getTaskStatus() == Task.Status.Finished) {
                    Bitmap bitmap = null;
                    if (httpLoadTask.getHandledData() != null) {
                        bitmap = (Bitmap)httpLoadTask.getHandledData();
                    }

                    if (image instanceof ImageWithData) {
                        ImageWithData imageWithData = (ImageWithData)image;
                        imageWithData.setBitmap(bitmap);
                    }

                    callback.completed(httpLoadTask, image, bitmap, httpLoadTask.getTaskError());
                }
            }
        });

        if (taskManager != null) {
            taskManager.addTask(httpLoadTask);
        }

        return httpLoadTask;
    }

    public interface LoadCallback {
        void completed(Task task, Image image, Bitmap bitmap, Error error);
    }
}
