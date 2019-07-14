package com.example.alexeyglushkov.taskmanager.task

import android.os.Handler
import android.os.Looper

/**
 * Created by alexeyglushkov on 18.06.17.
 */

class StackTaskProviderWithRestorableProviderTest : StackTaskProviderTest() {
    override fun prepareTaskProvider(): TaskProvider {
        return RestorableTaskProvider(StackTaskProvider(false, Handler(Looper.myLooper()), "TestId"))
    }
}
