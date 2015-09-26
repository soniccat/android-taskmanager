package com.playground;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.alexeyglushkov.taskmanager.task.Task;
import com.playground.ui.SettingsField;
import com.rssclient.controllers.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TaskConfigFragment extends Fragment {

    TestTaskConfig config;
    TaskConfigFragmentListener listener;

    SettingsField name;
    SettingsField type;
    SettingsField count;
    SettingsField startId;
    SettingsField duration;
    SettingsField priority;
    SettingsField loadPolicy;

    public TaskConfigFragment() {
    }

    public void setConfig(TestTaskConfig config) {
        this.config = config;
    }

    public TestTaskConfig getConfig() {
        return config;
    }

    public void setDoneListener(TaskConfigFragmentListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_task_config, container, false);
        view.findViewById(R.id.done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {

                    if (config == null) {
                        config = new TestTaskConfig();
                    }

                    save(config);
                    listener.onDonePressed();
                }
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bindViews();
        applyConfig();
    }

    private void bindViews() {
        name = (SettingsField)getView().findViewById(R.id.name);
        count = (SettingsField)getView().findViewById(R.id.count);
        type = (SettingsField)getView().findViewById(R.id.type);
        startId = (SettingsField)getView().findViewById(R.id.start_id);
        duration = (SettingsField)getView().findViewById(R.id.duration);
        priority = (SettingsField)getView().findViewById(R.id.priority);
        loadPolicy = (SettingsField)getView().findViewById(R.id.load_policy);
    }

    private void applyConfig() {
        if (config != null) {
            name.setString(config.name);
            type.setInt(config.type);
            count.setInt(config.count);
            startId.setInt(config.startId);
            duration.setInt(config.duration);
            loadPolicy.setInt(config.loadPolicy.ordinal());
            priority.setInt(config.priority);
        }
    }

    private void save(TestTaskConfig config) {
        config.name = name.getString();
        config.type = type.getInt();
        config.count = count.getInt();
        config.startId = startId.getInt();
        config.duration = duration.getInt();
        config.loadPolicy = loadPolicy.getInt() == 0 ? Task.LoadPolicy.SkipIfAdded : Task.LoadPolicy.CancelAdded;
        config.priority = priority.getInt();
    }

    public interface TaskConfigFragmentListener {
        void onDonePressed();
    }
}
