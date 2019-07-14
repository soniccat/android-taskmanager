package com.example.alexeyglushkov.taskmanager.task

import android.os.Handler

import com.example.alexeyglushkov.taskmanager.task.PriorityTaskProvider

/**
 * Created by alexeyglushkov on 30.08.15.
 */
class TestTaskProvider(handler: Handler, id: String) : PriorityTaskProvider(handler, id)
