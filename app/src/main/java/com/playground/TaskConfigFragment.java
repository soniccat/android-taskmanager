package com.playground;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.ga.task.Task;
import com.playground.ui.SettingsField;
import com.rssclient.controllers.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TaskConfigFragment extends Fragment {

    TestTaskConfig config;
    TaskConfigFragmentListener listener;

    public TaskConfigFragment() {
        // Required empty public constructor
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

    private void save(TestTaskConfig config) {
        SettingsField name = (SettingsField)getView().findViewById(R.id.name);
        SettingsField count = (SettingsField)getView().findViewById(R.id.count);
        SettingsField startId = (SettingsField)getView().findViewById(R.id.start_id);
        SettingsField duration = (SettingsField)getView().findViewById(R.id.duration);
        SettingsField priority = (SettingsField)getView().findViewById(R.id.priority);
        SettingsField loadPolicy = (SettingsField)getView().findViewById(R.id.load_policy);

        config.name = name.getString();
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
