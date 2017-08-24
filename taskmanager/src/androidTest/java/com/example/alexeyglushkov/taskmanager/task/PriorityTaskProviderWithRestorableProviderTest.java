package com.example.alexeyglushkov.taskmanager.task;

import android.os.Handler;
import android.os.Looper;
import android.support.test.runner.AndroidJUnit4;

import org.junit.runner.RunWith;

/**
 * Created by alexeyglushkov on 18.06.17.
 */

@RunWith(AndroidJUnit4.class)
public class PriorityTaskProviderWithRestorableProviderTest extends PriorityTaskProviderTest{
    @Override
    protected TaskProvider prepareTaskProvider() {
        return new RestorableTaskProvider(new PriorityTaskProvider(new Handler(Looper.myLooper()), "TestId"));
    }

    protected PriorityTaskProvider getPriorityTaskProvider() {
        RestorableTaskProvider restorableTaskProvider = (RestorableTaskProvider)taskProvider;
        return (PriorityTaskProvider)restorableTaskProvider.getProvider();
    }
}
