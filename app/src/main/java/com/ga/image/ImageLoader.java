package com.ga.image;

import android.graphics.Bitmap;

import com.ga.loader.data.BitmapReader;
import com.ga.loader.data.ByteArrayReader;
import com.ga.loader.http.HttpLoadTask;
import com.ga.task.Task;
import com.ga.task.TaskManager;
import com.ga.task.Tasks;

//Because Image doesn't store loaded data we should use ImageLoader to get the data from callback
public class ImageLoader {

    //TODO: write argument descriptions
    public static Task loadImage(TaskManager taskManager, final Image image, final ImageLoader.LoadCallback callback) {
        return loadImage(taskManager, image, null, callback);
    }

    public static Task loadImage(TaskManager taskManager, final Image image, String destinationId, final ImageLoader.LoadCallback callback) {
        final HttpLoadTask httpLoadTask = new HttpLoadTask(image.getUrlConnection(), new ByteArrayReader(new BitmapReader(null)));

        httpLoadTask.setLoadPolicy(image.getLoadPolicy());
        httpLoadTask.setContentLength(image.getByteSize());

        Tasks.bindOnTaskCompletion(httpLoadTask, image);

        if (httpLoadTask.getTaskId() != null && destinationId != null) {
            httpLoadTask.setTaskId(httpLoadTask.getTaskId() + destinationId);
        }

        httpLoadTask.setTaskCallback(new Task.Callback() {
            @Override
            public void finished(boolean cancelled) {
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
            taskManager.put(httpLoadTask);
        }

        return httpLoadTask;
    }

    public interface LoadCallback {
        void completed(Task task, Image image, Bitmap bitmap, Error error);
    }
}
