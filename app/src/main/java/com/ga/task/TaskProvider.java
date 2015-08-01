package com.ga.task;

import android.os.Handler;
import android.util.SparseArray;

import java.util.List;

/**
 * Created by alexeyglushkov on 30.12.14.
 */

// A provider defines in which order tasks should be executed
// A provider must remove a task from the pool in the takeTopTask method

public interface TaskProvider extends TaskPool {
    Task getTopTask(List<Integer> typesToFilter);
    Task takeTopTask(List<Integer> typesToFilter);
}
