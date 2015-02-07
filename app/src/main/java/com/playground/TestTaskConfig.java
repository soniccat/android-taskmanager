package com.playground;

import com.ga.task.Task;

import java.io.Serializable;

/**
 * Created by alexeyglushkov on 18.01.15.
 */
public class TestTaskConfig implements Serializable {

    private static final long serialVersionUID = 9120936709815714974L;

    public String name;
    public int count;
    public int startId;
    public int priority;
    public int duration;
    public int type;
    public Task.LoadPolicy loadPolicy;
}
