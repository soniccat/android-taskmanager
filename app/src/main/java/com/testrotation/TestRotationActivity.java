package com.testrotation;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.example.alexeyglushkov.taskmanager.task.RestorableTaskProvider;
import com.example.alexeyglushkov.taskmanager.task.SimpleTaskManagerSnapshot;
import com.example.alexeyglushkov.taskmanager.task.StackTaskProvider;
import com.example.alexeyglushkov.taskmanager.task.Task;
import com.example.alexeyglushkov.taskmanager.task.TaskImpl;
import com.example.alexeyglushkov.taskmanager.task.TaskManager;
import com.example.alexeyglushkov.taskmanager.task.TaskManagerSnapshot;
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

    private RestorableTaskProvider taskPool;
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
        taskPool = (RestorableTaskProvider) getTaskManager().getTaskProvider(POOL_NAME);

        if (taskPool == null) {
            StackTaskProvider stackProvider = new StackTaskProvider(true, getTaskManager().getHandler(), POOL_NAME);
            taskPool = new RestorableTaskProvider(stackProvider);

            getTaskManager().addTaskProvider(taskPool);

        } else {
            taskPool.restoreTaskCompletion(TASK_ID, getTaskCallback(this));
            // TODO: figure out when it's better to call that to not lose completed and active tasks
            //taskPool.setRecording(false);
        }
    }

    void stopTask() {
        Log.d(TAG, "cancel ref " + this);

        stopTasks(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        taskPool.setRecording(true);
    }

    static void startTask(TestRotationActivity activity) {
        Task task = new TaskImpl() {
            @Override
            public void startTask() {
                super.startTask();

                try {
                    for(int i=0; i<10; ++i) {
                        Thread.sleep(500);

                        if (getNeedCancelTask()) {
                            setIsCancelled();
                            break;
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                handleTaskCompletion();
            }
        };

        task.setTaskId(TASK_ID);
        task.setLoadPolicy(Task.LoadPolicy.CancelAdded);

        task.setTaskCallback(getTaskCallback(activity));

        activity.taskPool.addTask(task);
    }

    @NonNull
    private static Task.Callback getTaskCallback(TestRotationActivity activity) {
        final WeakReference<TestRotationActivity> ref = new WeakReference<>(activity);
        Log.d(TAG, "ref " + activity);

        return new Task.Callback() {
            @Override
            public void onCompleted(boolean cancelled) {
                TestRotationActivity currentActivity = ref.get();
                boolean isCompleted = currentActivity == null || (currentActivity.isFinishing() || currentActivity.isDestroyed());
                Log.d(TAG, "Button 1 task completed " + currentActivity + " is completed " + isCompleted + " cancelled " + cancelled);
            }
        };
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
