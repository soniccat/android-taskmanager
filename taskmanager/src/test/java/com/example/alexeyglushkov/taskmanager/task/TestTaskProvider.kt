package com.example.alexeyglushkov.taskmanager.task

import android.os.Handler

import com.example.alexeyglushkov.taskmanager.task.PriorityTaskProvider
import kotlinx.coroutines.CoroutineScope

/**
 * Created by alexeyglushkov on 30.08.15.
 */
open class TestTaskProvider(threadRunner: ThreadRunner, id: String) : PriorityTaskProvider(threadRunner, id)
