package com.playground;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.rssclient.controllers.R;

import java.util.ArrayList;
import java.util.List;

public class CreateTasksFragment extends Fragment {

    final String CREATE_CONFIG_FRAGMENT_TAG = "CREATE_TAG";
    final String CHANGE_CONFIG_FRAGMENT_TAG = "CHANGE_TAG";

    ArrayList<TestTaskConfig> taskConfigs;

    ListView list;
    TaskConfigAdapter configAdapter;
    CreateTaskFragmentListener listener;

    public CreateTasksFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        taskConfigs = new ArrayList<TestTaskConfig>();
        configAdapter = new TaskConfigAdapter(getActivity(), R.layout.create_task_layout_row, taskConfigs);
        configAdapter.setListener(new TaskConfigAdapter.TaskConfigAdapterListener() {
            @Override
            public void onTaskCreatePressed(TestTaskConfig config) {
                if (getListener() != null) {
                    getListener().onTaskCreatePressed(config);
                }
            }
        });

        list = (ListView)view.findViewById(R.id.list);
        list.setAdapter(configAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == configAdapter.getCount()-1) {
                    showAddConfigFragment();
                } else {
                    showEditConfigFragment(position);
                }
            }
        });
    }

    public CreateTaskFragmentListener getListener() {
        return listener;
    }

    public void setListener(CreateTaskFragmentListener listener) {
        this.listener = listener;
    }

    private void showAddConfigFragment() {
        final TaskConfigFragment changeFragment = new TaskConfigFragment();
        changeFragment.setDoneListener(new TaskConfigFragment.TaskConfigFragmentListener() {
            @Override
            public void onDonePressed() {
                TestTaskConfig config = changeFragment.getConfig();
                configAdapter.insert(config, configAdapter.getCount()-1);
                getFragmentManager().popBackStack();

                if (getListener() != null) {
                    getListener().onConfigCreated(config);
                }
            }
        });

        getFragmentManager().beginTransaction()
                .add(R.id.container, changeFragment, CREATE_CONFIG_FRAGMENT_TAG)
                .addToBackStack(null)
                .commit();
    }

    private void showEditConfigFragment(final int i) {
        final TestTaskConfig config = taskConfigs.get(i);
        final TaskConfigFragment changeFragment = new TaskConfigFragment();
        changeFragment.setConfig(config);
        changeFragment.setDoneListener(new TaskConfigFragment.TaskConfigFragmentListener() {
            @Override
            public void onDonePressed() {
                configAdapter.notifyDataSetChanged();
                getFragmentManager().popBackStack();

                if (getListener() != null) {
                    getListener().onConfigChanged(config);
                }
            }
        });

        getFragmentManager().beginTransaction()
                .add(R.id.container, changeFragment, CHANGE_CONFIG_FRAGMENT_TAG)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_tasks, container, false);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public List<TestTaskConfig> getConfigList() {
        return taskConfigs.subList(0, taskConfigs.size()-1);
    }

    public void setConfigList(List<TestTaskConfig> list) {
        if (taskConfigs.size() > 1) {
            taskConfigs.subList(0, taskConfigs.size()-1).clear();
        }

        int i = 0;
        for (TestTaskConfig config : list) {
            taskConfigs.add(i,config);
            ++i;
        }

        configAdapter.notifyDataSetChanged();
    }

    public interface CreateTaskFragmentListener {
        void onConfigCreated(TestTaskConfig config);
        void onConfigChanged(TestTaskConfig config);
        void onTaskCreatePressed(TestTaskConfig config);
    }
}
