package com.example.alexeyglushkov.taskmanager.task;

import android.os.Handler;
import android.os.Looper;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by alexeyglushkov on 08.08.15.
 */

public class SimpleTaskPoolTest extends TaskPoolTestSet {
    @Before
    public void setUp() throws Exception{
        SimpleTaskPoolTest.this.before(new SimpleTaskPool(new Handler(Looper.myLooper())));
    }

    @Test
    public void testSetGetHandler() {
        SimpleTaskPoolTest.this.setGetHandler();
    }

    @Test
    public void testAddTask() {
        SimpleTaskPoolTest.this.addTask();
    }

    @Test
    public void testAddStartedTask() {
        SimpleTaskPoolTest.this.addStartedTask();
    }

    @Test
    public void testRemoveTask() {
        SimpleTaskPoolTest.this.removeTask();
    }

    @Test
    public void testRemoveUnknownTask() {
        SimpleTaskPoolTest.this.removeUnknownTask();
    }

    @Test
    public void testGetTask() {
        SimpleTaskPoolTest.this.getTask();
    }

    @Test
    public void testGetUnknownTask() {
        SimpleTaskPoolTest.this.getUnknownTask();
    }

    @Test
    public void testGetTaskCount() {
        SimpleTaskPoolTest.this.getTaskCount();
    }

    @Test
    public void testGetTaskCount2() {
        SimpleTaskPoolTest.this.getTaskCount2();
    }

    @Test
    public void testSetGetUserData() {
        SimpleTaskPoolTest.this.setGetUserData();
    }

    @Test
    public void testAddStateListener() {
        SimpleTaskPoolTest.this.addStateListener();
    }

    @Test
    public void testRemoveStateListener() {
        SimpleTaskPoolTest.this.removeStateListener();
    }

    @Test
    public void testChangeTaskStatus() {
        SimpleTaskPoolTest.this.changeTaskStatus();
    }

    @Test
    public void testCheckTaskRemovingAfterFinishing() {
        SimpleTaskPoolTest.this.checkTaskRemovingAfterFinishing();
    }
}
