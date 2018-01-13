package com.playground;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.alexeyglushkov.streamlib.readersandwriters.ObjectReader;
import com.example.alexeyglushkov.taskmanager.file.FileKeepTask;
import com.example.alexeyglushkov.taskmanager.file.ObjectWriter;
import com.example.alexeyglushkov.taskmanager.loader.file.FileLoadTask;
import com.example.alexeyglushkov.taskmanager.task.SimpleTaskManagerSnapshot;
import com.example.alexeyglushkov.taskmanager.task.Task;
import com.example.alexeyglushkov.taskmanager.task.TaskManager;
import com.example.alexeyglushkov.taskmanager.task.TaskManagerSnapshot;
import com.example.alexeyglushkov.taskmanager.ui.TaskManagerView;
import com.main.MainApplication;
import com.rssclient.controllers.R;

import java.util.ArrayList;
import java.util.List;

public class PlaygroundActivity extends AppCompatActivity implements TaskManagerSnapshot.OnSnapshotChangedListener,
        CreateTasksFragment.CreateTaskFragmentListener {

    final String CONFIGS_FILE_NAME = "TextTaskConfig";

    protected CreateTasksFragment createTasksFragment;
    protected ChangeTasksFragment changeTasksFragment;

    TaskManagerSnapshot snapshot;
    TaskManager taskManager;
    TaskManagerView taskManagerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MainApplication application = (MainApplication) getApplication();
        taskManager = application.getTaskManager();

        setContentView(R.layout.activity_playground);

        taskManagerView = (TaskManagerView)findViewById(R.id.task_manager_view);
        createTasksFragment = new CreateTasksFragment();
        createTasksFragment.setListener(this);

        changeTasksFragment = new ChangeTasksFragment();

        ViewPager pager = (ViewPager)findViewById(R.id.view_pager);
        pager.setAdapter(createPagerAdapter());

        snapshot = new SimpleTaskManagerSnapshot();
        snapshot.startSnapshotRecording(taskManager);
        snapshot.addSnapshotListener(this);

        loadConfigList(new LoadTaskConfigCallback() {
            @Override
            public void completed(List<TestTaskConfig> configs, Error error) {
                if (configs != null) {
                    createTasksFragment.setConfigList(configs);
                }
            }
        });
    }


    @Override
    public void onConfigCreated(TestTaskConfig config) {
        storeConfigList(createTasksFragment.getConfigList());
    }

    @Override
    public void onConfigChanged(TestTaskConfig config) {
        storeConfigList(createTasksFragment.getConfigList());
    }

    @Override
    public void onTaskCreatePressed(TestTaskConfig config) {
        runTestTasks(config);
    }

    private List<TestTask> createTestTasks(TestTaskConfig config) {
        List<TestTask> tasks = new ArrayList<TestTask>();
        int startId = config.startId;

        for (int i=0; i<config.count; ++i) {
            TestTask task = createTestTask(config);
            task.setTaskId(Integer.toString(startId));
            tasks.add(task);

            ++startId;
        }

        return tasks;
    }

    private void runTestTasks(TestTaskConfig config) {
        List<TestTask> tasks = createTestTasks(config);
        for (TestTask task : tasks) {
            taskManager.addTask(task);
        }
    }

    private TestTask createTestTask(TestTaskConfig config) {
        TestTask testTask = new TestTask(config.duration);
        testTask.setTaskPriority(config.priority);
        testTask.setTaskType(config.type);
        testTask.setLoadPolicy(config.loadPolicy);

        return testTask;
    }

    @Override
    public void onSnapshotChanged(TaskManagerSnapshot snapshot) {
        taskManagerView.showSnapshot(snapshot);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_playground, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private PagerAdapter createPagerAdapter() {
        return new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                if (position == 0) {
                    return createTasksFragment;

                } else if (position == 1) {
                    return changeTasksFragment;
                }

                return null;
            }

            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                if (position == 0) {
                    return getResources().getString(R.string.playground_create_tasks_title);
                } else if (position == 1) {
                    return getResources().getString(R.string.playground_change_tasks_title);
                }

                return "";
            }
        };
    }

    private void storeConfigList(List<TestTaskConfig> configList) {
        List<TestTaskConfig> listCopy = new ArrayList<TestTaskConfig>(configList);
        taskManager.addTask(new FileKeepTask(CONFIGS_FILE_NAME, new ObjectWriter(listCopy), this));
    }

    private void loadConfigList(final LoadTaskConfigCallback callback) {
        final FileLoadTask fileLoadTask = new FileLoadTask(CONFIGS_FILE_NAME, new ObjectReader(null), this);
        fileLoadTask.setTaskCallback(new Task.Callback() {
            @Override
            public void onCompleted(boolean cancelled) {
                List<TestTaskConfig> configs = null;
                if (fileLoadTask.getTaskResult() != null) {
                    configs = (List<TestTaskConfig>)fileLoadTask.getTaskResult();
                }

                if (callback != null) {
                    callback.completed(configs, fileLoadTask.getTaskError());
                }
            }
        });

        taskManager.addTask(fileLoadTask);
    }

    public interface LoadTaskConfigCallback {
        void completed(List<TestTaskConfig> configs, Error error);
    }
}
