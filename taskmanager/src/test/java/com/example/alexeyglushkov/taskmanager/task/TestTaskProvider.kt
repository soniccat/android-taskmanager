package com.example.alexeyglushkov.taskmanager.task

import com.example.alexeyglushkov.taskmanager.providers.PriorityTaskProvider
import com.example.alexeyglushkov.taskmanager.runners.ThreadRunner

/**
 * Created by alexeyglushkov on 30.08.15.
 */
open class TestTaskProvider(threadRunner: ThreadRunner, id: String) : PriorityTaskProvider(threadRunner, id)
