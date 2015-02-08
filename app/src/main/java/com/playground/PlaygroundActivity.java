package com.playground;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;

import com.ga.keeper.file.FileKeepTask;
import com.ga.keeper.file.ObjectWriter;
import com.ga.loader.data.ObjectHandler;
import com.ga.loader.data.ObjectReader;
import com.ga.loader.file.FileLoadTask;
import com.ga.task.Task;
import com.ga.task.TaskManager;
import com.ga.ui.TaskManagerView;
import com.main.MainApplication;
import com.rssclient.controllers.R;

import java.util.ArrayList;
import java.util.List;

public class PlaygroundActivity extends ActionBarActivity implements TaskManager.OnSnapshotChangedListener {

    final String CONFIGS_FILE_NAME = "TextTaskConfig";

    protected CreateTasksFragment createTasksFragment;
    protected ChangeTasksFragment changeTasksFragment;

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
        createTasksFragment.setListener(new CreateTasksFragment.CreateTaskFragmentListener() {
            @Override
            public void onConfigCreated(TestTaskConfig config) {
                storeConfigList(createTasksFragment.getConfigList());
            }

            @Override
            public void onConfigChanged(TestTaskConfig config) {
                storeConfigList(createTasksFragment.getConfigList());
            }
        });

        changeTasksFragment = new ChangeTasksFragment();

        ViewPager pager = (ViewPager)findViewById(R.id.view_pager);
        pager.setAdapter(createPagerAdapter());

        taskManager.addSnapshotListener(this);
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
    public void onSnapshotChanged(TaskManager.TaskManagerSnapshot snapshot) {
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
        taskManager.put(new FileKeepTask(CONFIGS_FILE_NAME, new ObjectWriter(listCopy), this));
    }

    private void loadConfigList(final LoadTaskConfigCallback callback) {
        final FileLoadTask fileLoadTask = new FileLoadTask(CONFIGS_FILE_NAME, new ObjectReader(null), this);
        fileLoadTask.setTaskCallback(new Task.Callback() {
            @Override
            public void finished(boolean cancelled) {
                List<TestTaskConfig> configs = null;
                if (fileLoadTask.getHandledData() != null) {
                    configs = (List<TestTaskConfig>)fileLoadTask.getHandledData();
                }

                if (callback != null) {
                    callback.completed(configs, fileLoadTask.getTaskError());
                }
            }
        });

        taskManager.put(fileLoadTask);
    }

    public interface LoadTaskConfigCallback {
        void completed(List<TestTaskConfig> configs, Error error);
    }
}
