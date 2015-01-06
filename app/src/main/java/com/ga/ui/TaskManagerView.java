package com.ga.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.rssreader.R;
import com.ga.task.TaskManager;

/**
 * Created by alexeyglushkov on 06.01.15.
 */
public class TaskManagerView extends LinearLayout {

    TaskManager.TaskManagerSnapshot snapshot;
    TaskBarView barView;
    TextView loadingTasks;
    TextView waitingTasks;

    public TaskManagerView(Context context) {
        super(context);
    }

    public TaskManagerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TaskManagerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        barView = (TaskBarView) findViewById(R.id.bar);
        loadingTasks = (TextView)findViewById(R.id.loading);
        waitingTasks = (TextView) findViewById(R.id.waiting);
    }

    public void showSnapshot(TaskManager.TaskManagerSnapshot snapshot) {
        this.snapshot = snapshot;

        updateLoadingTasks();
    }

    void updateLoadingTasks() {
        StringBuilder str = new StringBuilder();
        str.append("Waiting: ");
        str.append(snapshot.getLoadingTasksCount());

        loadingTasks.setText(str);
    }
}
