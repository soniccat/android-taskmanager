package com.aglushkov.taskmanager_http.image

import android.graphics.Bitmap
import com.aglushkov.taskmanager_http.loader.http.HTTPConnectionStreamReader
import com.aglushkov.taskmanager_http.loader.http.HTTPConnectionStreamReaderAdaptor
import com.aglushkov.taskmanager_http.loader.http.HttpTaskTransport
import com.aglushkov.taskmanager_http.loader.http.TransportTask
import com.example.alexeyglushkov.streamlib.convertors.BytesBitmapConverter
import com.example.alexeyglushkov.streamlib.data_readers_and_writers.ByteArrayReader
import com.example.alexeyglushkov.streamlib.data_readers_and_writers.InputStreamDataReader
import com.example.alexeyglushkov.taskmanager.task.Task

open class ImageTask(val image: Image, destinationId: String? = null): TransportTask(
        HttpTaskTransport(image,
                    HTTPConnectionStreamReaderAdaptor(
                            ByteArrayReader(BytesBitmapConverter())
                    )
                ).apply {
            contentLength = image.byteSize
        }
) {
    override var taskResult: Any?
        get() = super.taskResult
        set(value) {
            super.taskResult = value

            if (image is ImageWithData) {
                image.bitmap = value as? Bitmap
            }
        }

    init {
        loadPolicy = Task.LoadPolicy.CompleteWhenAlreadyAddedCompletes

        if (taskId != null && destinationId != null) {
            taskId += destinationId

        } else if (taskId != null) {
            taskId += image.hashCode()
        }
    }
}
