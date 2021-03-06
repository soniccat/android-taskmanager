package com.example.alexeyglushkov.taskmanager.file

import java.io.File
import android.content.Context

import com.example.alexeyglushkov.streamlib.data_readers_and_writers.InputStreamDataReader
import com.example.alexeyglushkov.streamlib.data_readers_and_writers.InputStreamDataReaders
import com.example.alexeyglushkov.taskmanager.task.TaskImpl

class FileLoadTask(protected var fileName: String,
                   protected var reader: InputStreamDataReader<*>,
                   context: Context) : TaskImpl() {
    val context: Context

    init {
        this.context = context.applicationContext
    }

    protected val fileSize: Long
        get() {
            val file = File(context.filesDir, fileName)
            return file.length()
        }

    override suspend fun startTask() {
        val name = this.fileName

        try {
            reader.setProgressUpdater(private.createProgressUpdater(fileSize.toFloat()))

            val fis = this.context.openFileInput(name)
            val data = InputStreamDataReaders.readOnce(reader, fis)
            taskResult = data

        } catch (e: Exception) {
            private.taskError = Exception("FileLoadTask exception: " + e.message)
        }
    }
}
