package com.aglushkov.taskmanager_http.image

import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import com.aglushkov.taskmanager_http.loader.http.HTTPConnectionStreamReader
import com.aglushkov.taskmanager_http.loader.http.HTTPConnectionStreamReaderAdaptor
import com.aglushkov.taskmanager_http.loader.http.HttpTaskTransport
import com.aglushkov.taskmanager_http.loader.http.TransportTask
import com.example.alexeyglushkov.streamlib.convertors.BytesBitmapConverter
import com.example.alexeyglushkov.streamlib.data_readers_and_writers.ByteArrayReader
import com.example.alexeyglushkov.streamlib.data_readers_and_writers.InputStreamDataReader
import com.example.alexeyglushkov.taskmanager.task.Task
import com.example.alexeyglushkov.taskmanager.task.Task.Callback
import com.example.alexeyglushkov.taskmanager.task.Tasks.bindOnTaskCompletion
import com.example.alexeyglushkov.taskmanager.task.ThreadRunner
import com.example.alexeyglushkov.tools.HandlerTools
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.junit.Assert

// Because Image doesn't store loaded data we should use ImageLoader to get the data from callback
//TODO: need to simplify this logic and remove static methods...
class ImageLoader {
    //TODO: write argument descriptions
    fun loadImage(threadRunner: ThreadRunner, image: Image, callback: LoadCallback?): Task {
        return loadImage(threadRunner, image, null, callback)
    }

    fun loadImage(threadRunner: ThreadRunner, image: Image, destinationId: String?, callback: LoadCallback?): Task {
        val streamReader: InputStreamDataReader<Bitmap> = ByteArrayReader(BytesBitmapConverter())
        val reader: HTTPConnectionStreamReader<Bitmap> = HTTPConnectionStreamReaderAdaptor(streamReader)
        val transportTask = createTask(image, destinationId, reader)
        val taskCallback = getTaskCallback(transportTask, image, callback)
        transportTask.taskCallback = taskCallback

        //threadRunner.launch {
            // to have addTaskStatusListener called on a scope's thread
            bindOnTaskCompletion(transportTask, image)
        //}

        // TODO: need to check case when a task was refused by a tak provider
        // maybe it's ok to set waiting on a scope's thread, to be able to bind on that before adding
        // and handle it in bindOnTaskCompletion listener
        Assert.assertEquals(Looper.myLooper(), Looper.getMainLooper())
        image.setTaskInProgress(transportTask)
        return transportTask
    }

    private fun createTask(image: Image, destinationId: String?, reader: HTTPConnectionStreamReader<Bitmap>): TransportTask {
        val transport = HttpTaskTransport(image, reader)
        transport.contentLength = image.byteSize

        val transportTask = TransportTask(transport)
        transportTask.loadPolicy = image.loadPolicy

        if (transportTask.taskId != null && destinationId != null) {
            transportTask.taskId = transportTask.taskId + destinationId
        } else if (transportTask.taskId != null) {
            transportTask.taskId = transportTask.taskId + image.hashCode()
        }

        return transportTask
    }

    fun getTaskCallback(transportTask: TransportTask, image: Image?, callback: LoadCallback?): Callback {
        return object : Callback {
            override fun onCompleted(cancelled: Boolean) { //ignore a cancelled result
                if (callback != null && transportTask.taskStatus === Task.Status.Finished) {
                    var bitmap: Bitmap? = null
                    if (transportTask.taskResult != null) {
                        bitmap = transportTask.taskResult as Bitmap?
                    }
                    if (image is ImageWithData) {
                        image.bitmap = bitmap
                    }
                    callback.completed(transportTask, image, bitmap, transportTask.taskError)
                }
            }
        }
    }

    interface LoadCallback {
        fun completed(task: Task?, image: Image?, bitmap: Bitmap?, error: Error?)
    }
}