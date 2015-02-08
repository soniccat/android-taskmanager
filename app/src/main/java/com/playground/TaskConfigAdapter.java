package com.playground;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.rssclient.controllers.R;

import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

/**
 * Created by alexeyglushkov on 18.01.15.
 */
public class TaskConfigAdapter extends ArrayAdapter {

    private TaskConfigAdapterListener listener;

    public TaskConfigAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);

        add(context.getResources().getString(R.string.create_task_cell_create_new_title));
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return position == getCount() - 1 ? 0 : 1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View resultView = null;
        if (position == getCount() - 1) {
            if (convertView == null) {
                resultView = View.inflate(getContext(),android.R.layout.activity_list_item, null);
                TextView textView = (TextView)resultView.findViewById(android.R.id.text1);
                textView.setText((String)getItem(position));

            } else {
                resultView = convertView;
            }

        } else {
            resultView = convertView;

            if (resultView == null) {
                resultView = View.inflate(getContext(),R.layout.create_task_layout_row, null);
                resultView.setTag(new TaskConfigRowHolder(resultView));
            }

            final TestTaskConfig config = (TestTaskConfig)getItem(position);
            TaskConfigRowHolder holder = (TaskConfigRowHolder)resultView.getTag();

            holder.name.setText(config.name);
            holder.create.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getListener() != null) {
                        getListener().onTaskCreatePressed(config);
                    }
                }
            });
        }

        return resultView;
    }

    public TaskConfigAdapterListener getListener() {
        return listener;
    }

    public void setListener(TaskConfigAdapterListener listener) {
        this.listener = listener;
    }

    private class TaskConfigRowHolder {
        public TextView name;
        public Button create;

        public TaskConfigRowHolder(View view) {
            name = (TextView)view.findViewById(R.id.name);
            create = (Button)view.findViewById(R.id.create_task);
        }
    }

    public interface TaskConfigAdapterListener {
        void onTaskCreatePressed(TestTaskConfig config);
    }
}
