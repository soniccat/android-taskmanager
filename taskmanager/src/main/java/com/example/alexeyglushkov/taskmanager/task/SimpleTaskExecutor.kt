package com.example.alexeyglushkov.taskmanager.task

import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

/**
 * Created by alexeyglushkov on 08.02.15.
 */
class SimpleTaskExecutor : TaskExecutor {

    override fun executeTask(task: Task, callback: Task.Callback) {
        val thread = sThreadFactory.newThread {
            if (!Tasks.isTaskCompleted(task)) { // the task could be already handled by task manager
                task.startTask()
            }
        }

        thread.start()
    }

    companion object {

        private val sThreadFactory = object : ThreadFactory {
            private val mCount = AtomicInteger(1)

            override fun newThread(r: Runnable): Thread {
                return Thread(r, "AsyncTask #" + mCount.getAndIncrement())
            }
        }
    }
}
