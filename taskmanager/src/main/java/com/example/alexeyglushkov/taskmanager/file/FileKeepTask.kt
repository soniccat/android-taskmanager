package com.example.alexeyglushkov.taskmanager.file

import android.content.Context

import com.example.alexeyglushkov.streamlib.data_readers_and_writers.OutputStreamDataWriter
import com.example.alexeyglushkov.streamlib.data_readers_and_writers.OutputStreamDataWriters
import com.example.alexeyglushkov.taskmanager.task.SimpleTask

class FileKeepTask<T>(private val fileName: String,
                      private val writer: OutputStreamDataWriter<T>,
                      private val data: T,
                      context: Context) : SimpleTask() {

    val context: Context

    init {
        this.context = context.applicationContext
    }

    override suspend fun startTask() {
        val name = this.fileName
        try {
            val fos = context.openFileOutput(name, Context.MODE_PRIVATE)
            OutputStreamDataWriters.writeOnce<T>(writer, fos, data)

        } catch (e: Exception) {
            e.printStackTrace()
            taskError = Exception("FileKeepTask exception: " + e.message)
        }

        private.handleTaskCompletion()
    }
}
