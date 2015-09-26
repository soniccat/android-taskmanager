package com.playground;

import com.example.alexeyglushkov.taskmanager.task.SimpleTask;

/**
 * Created by alexeyglushkov on 18.01.15.
 */
public class TestTask extends SimpleTask {

    int workTime = 1000;
    int chunkTimeSize = 10;

    public TestTask(int workTime) {
        this.workTime = workTime;
    }

    public void startTask() {
        int chunkCount = (int)((float)workTime / (float)chunkTimeSize);
        for (int i=0; i<chunkCount; ++i) {
            try {
                Thread.currentThread().sleep(chunkTimeSize);
            } catch (Exception ex) {
            }
        }

        getPrivate().handleTaskCompletion();
        return;
    }
}
