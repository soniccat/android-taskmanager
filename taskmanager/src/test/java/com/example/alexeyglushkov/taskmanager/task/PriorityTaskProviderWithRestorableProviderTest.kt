package com.example.alexeyglushkov.taskmanager.task

import android.os.Handler
import android.os.Looper

/**
 * Created by alexeyglushkov on 18.06.17.
 */

class PriorityTaskProviderWithRestorableProviderTest : PriorityTaskProviderTest() {

    override val priorityTaskProvider: PriorityTaskProvider
        get() {
            val restorableTaskProvider = taskProvider as RestorableTaskProvider
            return restorableTaskProvider.provider as PriorityTaskProvider
        }

    override fun prepareTaskProvider(): TaskProvider {
        return RestorableTaskProvider(PriorityTaskProvider(Handler(Looper.myLooper()), "TestId"))
    }
}
