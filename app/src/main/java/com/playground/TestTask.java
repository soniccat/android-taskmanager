package com.playground;

import com.ga.task.AsyncTask;

/**
 * Created by alexeyglushkov on 18.01.15.
 */
public class TestTask extends AsyncTask {

    int workTime = 1000;
    int chunkTimeSize = 10;

    public TestTask(int workTime) {
        this.workTime = workTime;
    }

    @Override
    protected Void doInBackground(Void... params) {

        int chunkCount = (int)((float)workTime / (float)chunkTimeSize);
        for (int i=0; i<chunkCount; ++i) {
            try {
                Thread.currentThread().sleep(chunkTimeSize);
            } catch (Exception ex) {
            }
        }

        handleTaskCompletion();
        return null;
    }
}
