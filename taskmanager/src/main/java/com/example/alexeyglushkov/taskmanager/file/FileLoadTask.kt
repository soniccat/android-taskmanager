package com.example.alexeyglushkov.taskmanager.file

import java.io.BufferedInputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import android.content.Context

import com.example.alexeyglushkov.streamlib.data_readers_and_writers.InputStreamDataReader
import com.example.alexeyglushkov.streamlib.data_readers_and_writers.InputStreamDataReaders
import com.example.alexeyglushkov.streamlib.data_readers_and_writers.OutputStreamDataWriters
import com.example.alexeyglushkov.taskmanager.task.SimpleTask

class FileLoadTask(protected var fileName: String, protected var reader: InputStreamDataReader<*>, protected var context: Context?) : SimpleTask() {

    protected val fileSize: Long
        get() {
            val file = File(context!!.filesDir, fileName)
            return file.length()
        }

    override fun startTask(callback: Task.Callback) {
        super.startTask(callback)

        if (context != null) {
            val name = this.fileName

            try {
                reader.setProgressUpdater(private.createProgressUpdater(fileSize.toFloat()))

                val fis = this.context!!.openFileInput(name)
                val data = InputStreamDataReaders.readOnce(reader, fis)
                taskResult = data

            } catch (e: Exception) {
                private.taskError = Error("FileLoadTask exception: " + e.message)
            }

        } else {
            private.taskError = Error("Context is null")
        }

        private.handleTaskCompletion(callback)
    }
}
