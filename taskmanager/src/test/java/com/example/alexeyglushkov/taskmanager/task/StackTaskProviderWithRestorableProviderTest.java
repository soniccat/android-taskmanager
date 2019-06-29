package com.example.alexeyglushkov.taskmanager.task;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by alexeyglushkov on 18.06.17.
 */

public class StackTaskProviderWithRestorableProviderTest extends StackTaskProviderTest{
    @Override
    protected TaskProvider prepareTaskProvider() {
        return new RestorableTaskProvider(new StackTaskProvider(false, new Handler(Looper.myLooper()), "TestId"));
    }
}
