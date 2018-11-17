package com.example.alexeyglushkov.taskmanager.task;

import android.os.Handler;
import android.os.Looper;
import androidx.test.runner.AndroidJUnit4;

import org.junit.runner.RunWith;

/**
 * Created by alexeyglushkov on 18.06.17.
 */

@RunWith(AndroidJUnit4.class)
public class StackTaskProviderWithRestorableProviderTest extends StackTaskProviderTest{
    @Override
    protected TaskProvider prepareTaskProvider() {
        return new RestorableTaskProvider(new StackTaskProvider(false, new Handler(Looper.myLooper()), "TestId"));
    }
}
