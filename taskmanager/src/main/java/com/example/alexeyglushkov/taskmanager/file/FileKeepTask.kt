package com.example.alexeyglushkov.taskmanager.file

import android.content.Context

import com.example.alexeyglushkov.streamlib.data_readers_and_writers.OutputStreamDataWriter
import com.example.alexeyglushkov.streamlib.data_readers_and_writers.OutputStreamDataWriters
import com.example.alexeyglushkov.taskmanager.task.SimpleTask

class FileKeepTask<T>(private val fileName: String,
                      private val writer: OutputStreamDataWriter<T>,
                      private val data: T, private val context: Context?) : SimpleTask() {

    override fun startTask() {
        super.startTask()

        if (context != null) {
            val name = this.fileName
            try {
                val fos = context.openFileOutput(name, Context.MODE_PRIVATE)
                OutputStreamDataWriters.writeOnce<T>(writer, fos, data)

            } catch (e: Exception) {
                e.printStackTrace()
                taskError = Error("FileKeepTask exception: " + e.message)
            }
        } else {
            taskError = Error("Context is null")
        }

        private.handleTaskCompletion()
    }
}
