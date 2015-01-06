package com.ga.ui;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.rssreader.R;
import com.ga.task.TaskManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by alexeyglushkov on 06.01.15.
 */
public class TaskManagerView extends LinearLayout {

    TaskManager.TaskManagerSnapshot snapshot;
    TaskBarView barView;
    TextView loadingTasks;
    TextView waitingTasks;
    List<Integer> colors;

    List<Integer> allTypes;

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

        colors = new ArrayList<Integer>();
        colors.addAll(Arrays.asList(Color.CYAN, Color.GREEN, Color.BLUE, Color.MAGENTA));

        barView = (TaskBarView) findViewById(R.id.bar);
        loadingTasks = (TextView)findViewById(R.id.loading);
        waitingTasks = (TextView) findViewById(R.id.waiting);
    }

    public void showSnapshot(TaskManager.TaskManagerSnapshot snapshot) {
        this.snapshot = snapshot;

        updateAllTypesArray();
        updateLoadingTasks();
        updateWaitingTasks();
    }

    void updateLoadingTasks() {
        StringBuilder str = new StringBuilder();
        str.append("Loading: ");
        str.append(snapshot.getLoadingTasksCount() + " ");

        SparseArray<Integer> loadingTaskInfo = snapshot.getUsedLoadingSpace();
        int count;
        for (int type : allTypes) {
            count = loadingTaskInfo.get(type, 0);
            str.append(count + " ");
        }

        loadingTasks.setText(str);
    }

    void updateWaitingTasks() {
        StringBuilder str = new StringBuilder();
        str.append("Waiting: ");
        str.append(snapshot.getWaitingTasksCount() + " ");

        SparseArray<Integer> loadingTaskInfo = snapshot.getWaitingTaskInfo();
        int count;
        for (int type : allTypes) {
            count = loadingTaskInfo.get(type, 0);
            str.append(count + " ");
        }

        waitingTasks.setText(str);
    }

    private void updateAllTypesArray() {
        Set<Integer> allTypesSet = new HashSet<Integer>();

        SparseArray<Integer> loadingTaskInfo = snapshot.getUsedLoadingSpace();
        SparseArray<Float> loadingLimits = snapshot.getLoadingLimits();
        SparseArray<Integer> waitingTaskInfo = snapshot.getWaitingTaskInfo();

        //fill types array
        int type;
        for (int i = 0; i < loadingTaskInfo.size(); i++) {
            type = loadingTaskInfo.keyAt(i);
            allTypesSet.add(type);
        }

        for (int i = 0; i < loadingLimits.size(); i++) {
            type = loadingLimits.keyAt(i);
            allTypesSet.add(type);
        }

        for (int i = 0; i < waitingTaskInfo.size(); i++) {
            type = waitingTaskInfo.keyAt(i);
            allTypesSet.add(type);
        }

        Integer[] allTypesArray = new Integer[allTypesSet.size()];
        allTypesSet.toArray(allTypesArray);
        Arrays.sort(allTypesArray);
        allTypes = Arrays.asList(allTypesArray);
    }
}
