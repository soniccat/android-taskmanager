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

class ImageLoader {
    fun createTask(image: Image): Task {
        return createTask(image, null)
    }

    fun createTask(image: Image, destinationId: String?): Task {
        val streamReader: InputStreamDataReader<Bitmap> = ByteArrayReader(BytesBitmapConverter())
        val reader: HTTPConnectionStreamReader<Bitmap> = HTTPConnectionStreamReaderAdaptor(streamReader)
        return createTask(image, destinationId, reader)
    }

    private fun createTask(image: Image, destinationId: String?, reader: HTTPConnectionStreamReader<Bitmap>): TransportTask {
        val transport = HttpTaskTransport(image, reader)
        transport.contentLength = image.byteSize

        val transportTask = object : TransportTask(transport) {
            override var taskResult: Any?
                get() = super.taskResult
                set(value) {
                    super.taskResult = value

                    if (image is ImageWithData) {
                        image.bitmap = value as? Bitmap
                    }
                }
        }

        transportTask.loadPolicy = image.loadPolicy

        if (transportTask.taskId != null && destinationId != null) {
            transportTask.taskId = transportTask.taskId + destinationId

        } else if (transportTask.taskId != null) {
            transportTask.taskId = transportTask.taskId + image.hashCode()
        }

        return transportTask
    }
}
