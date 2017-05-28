package com.testrotation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.example.alexeyglushkov.taskmanager.task.SimpleTaskManagerSnapshot;
import com.example.alexeyglushkov.taskmanager.task.StackTaskProvider;
import com.example.alexeyglushkov.taskmanager.task.Task;
import com.example.alexeyglushkov.taskmanager.task.TaskImpl;
import com.example.alexeyglushkov.taskmanager.task.TaskManager;
import com.example.alexeyglushkov.taskmanager.task.TaskManagerSnapshot;
import com.example.alexeyglushkov.taskmanager.task.TaskPool;
import com.example.alexeyglushkov.taskmanager.task.TaskProvider;
import com.example.alexeyglushkov.taskmanager.ui.TaskBarView;
import com.example.alexeyglushkov.taskmanager.ui.TaskManagerView;
import com.main.MainApplication;
import com.rssclient.controllers.R;

/**
 * Created by alexeyglushkov on 28.05.17.
 */

public class TestRotationActivity extends AppCompatActivity {
    final String TAG = "Test_Rotation";
    final String POOL_NAME = "rotationStack";

    private TaskProvider taskPool;
    private TaskManagerSnapshot snapshot;
    private TaskManagerView taskManagerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_rotation_test);

        taskManagerView = (TaskManagerView) findViewById(R.id.task_manager_view);

        View view = findViewById(R.id.button1);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTask();
            }
        });

        snapshot = new SimpleTaskManagerSnapshot();
        snapshot.startSnapshotRecording(getTaskManager());
        snapshot.addSnapshotListener(new TaskManagerSnapshot.OnSnapshotChangedListener() {
            @Override
            public void onSnapshotChanged(TaskManagerSnapshot snapshot) {
                taskManagerView.showSnapshot(snapshot);
            }
        });

        initializeTaskPool();
    }

    private void initializeTaskPool() {
        //taskPool = getTaskManager().getTaskProvider(POOL_NAME);
        if (taskPool == null) {
            taskPool = new StackTaskProvider(true, getTaskManager().getHandler(), POOL_NAME);
            getTaskManager().addTaskProvider(taskPool);
        }
    }

    void startTask() {
        Task task = new TaskImpl() {
            @Override
            public void startTask(Callback callback) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                handleTaskCompletion(callback);
            }
        };

        task.setTaskId("Button 1");
        task.setLoadPolicy(Task.LoadPolicy.CancelAdded);
        task.setTaskCallback(new Task.Callback() {
            @Override
            public void onCompleted(boolean cancelled) {
                Log.d(TAG, "Button 1 task completed");
            }
        });

        taskPool.addTask(task);
    }

    //// Getters

    TaskManager getTaskManager() {
        return MainApplication.instance.getTaskManager();
    }
}
