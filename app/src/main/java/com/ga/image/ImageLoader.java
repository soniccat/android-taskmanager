package com.ga.image;

import android.graphics.Bitmap;

import com.ga.task.DataFormat;
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
        final HttpLoadTask httpLoadTask = new HttpLoadTask(image.getUrlConnection(), image.getDataHandler());
        httpLoadTask.setLoadPolicy(image.getLoadPolicy());

        Tasks.bindOnTaskCompletion(httpLoadTask, image);

        if (httpLoadTask.getTaskId() != null && destinationId != null) {
            httpLoadTask.setTaskId(httpLoadTask.getTaskId() + destinationId);
        }

        taskManager.put(httpLoadTask, new Task.Callback() {
            @Override
            public void finished() {
                //ignore a cancelled result
                if (callback != null && httpLoadTask.getTaskStatus() == Task.Status.Finished) {
                    Bitmap bitmap = null;
                    if (httpLoadTask.getData() != null) {
                        bitmap = Image.bitmapFromByteArray(httpLoadTask.getData());
                    }

                    callback.completed(httpLoadTask, image, bitmap, httpLoadTask.getTaskError());
                }
            }
        });

        return httpLoadTask;
    }

    public interface LoadCallback {
        void completed(Task task, Image image, Bitmap bitmap, Error error);
    }
}
