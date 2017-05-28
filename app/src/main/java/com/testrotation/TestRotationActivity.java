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
import com.example.alexeyglushkov.taskmanager.task.WeakRefList;
import com.example.alexeyglushkov.taskmanager.ui.TaskBarView;
import com.example.alexeyglushkov.taskmanager.ui.TaskManagerView;
import com.main.MainApplication;
import com.rssclient.controllers.R;

import java.lang.ref.WeakReference;

/**
 * Created by alexeyglushkov on 28.05.17.
 */

public class TestRotationActivity extends AppCompatActivity {
    static final String TAG = "Test_Rotation";
    static final String POOL_NAME = "rotationStack";
    static final String TASK_ID = "Button 1";

    private TaskProvider taskPool;
    private TaskManagerSnapshot snapshot;
    private TaskManagerView taskManagerView;

    private Task lastTask;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_rotation_test);

        taskManagerView = (TaskManagerView) findViewById(R.id.task_manager_view);

        View view = findViewById(R.id.button1);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTask(TestRotationActivity.this);
            }
        });

        view = findViewById(R.id.button2);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopTask();
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
        taskPool = getTaskManager().getTaskProvider(POOL_NAME);
        if (taskPool == null) {
            taskPool = new StackTaskProvider(true, getTaskManager().getHandler(), POOL_NAME);
            getTaskManager().addTaskProvider(taskPool);
        }
    }

    void stopTask() {
        Log.d(TAG, "cancel ref " + this);

        stopTasks(this);
    }

    static void startTask(TestRotationActivity activity) {
        Task task = new TaskImpl() {
            @Override
            public void startTask(Callback callback) {
                try {
                    for(int i=0; i<30; ++i) {
                        Thread.sleep(1000);

                        if (getNeedCancelTask()) {
                            setIsCancelled();
                            break;
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                handleTaskCompletion(callback);
            }
        };

        task.setTaskId(TASK_ID);
        task.setLoadPolicy(Task.LoadPolicy.CancelAdded);

        final WeakReference<TestRotationActivity> ref = new WeakReference<>(activity);
        Log.d(TAG, "ref " + activity);

        task.setTaskCallback(new Task.Callback() {
            @Override
            public void onCompleted(boolean cancelled) {
                Log.d(TAG, "Button 1 task completed " + ref.get());
            }
        });

        activity.taskPool.addTask(task);
    }

    static private void stopTasks(final TestRotationActivity activity) {
        final TaskManager manager = activity.getTaskManager();
        manager.getHandler().post(new Runnable() {
            @Override
            public void run() {
                Task task = manager.getTask(TASK_ID);
                if (task != null) {
                    manager.cancel(task, null);
                }
            }
        });
    }

    //// Getters

    TaskManager getTaskManager() {
        return MainApplication.instance.getTaskManager();
    }
}
