package com.rssclient.controllers;

import java.util.ArrayList;

import com.main.MainApplication;
import com.rssclient.model.RssFeed;
import com.rssclient.model.RssStorage;
import com.rssclient.model.RssItem;
import com.ga.task.TaskManager;
import com.ga.ui.TaskManagerView;
import com.google.common.collect.Range;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;


public class RssItemsActivity extends ActionBarActivity implements RssItemsAdapter.RssItemsAdapterListener, TaskManager.OnSnapshotChangedListener {

    TaskManager taskManager;
    ListView listView;
    RssStorage rssStorage;
    TaskManagerView taskManagerView;

    RssFeed feed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rss_items);

        MainApplication application = (MainApplication) getApplication();

        Intent intent = getIntent();
        feed = (RssFeed) intent.getSerializableExtra(MainRssActivity.FEED_OBJECT);

        taskManager = application.getTaskManager();
        rssStorage = application.getRssStorage();
        
        taskManagerView = (TaskManagerView) findViewById(R.id.task_manager_view);
        taskManager.startSnapshotRecording();
        taskManager.addSnapshotListener(this);

        listView = (ListView) this.findViewById(R.id.listview);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                final RssItemsAdapter adapter = (RssItemsAdapter)view.getAdapter();
                if (adapter != null) {
                    adapter.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
                }
            }
        });

        final RssItemsActivity acitvity = this;

        rssStorage.loadFeed(taskManager, this, feed, new RssStorage.RssFeedCallback() {

            @Override
            public void completed(RssFeed feed, final Error error) {
                // TODO Auto-generated method stub
                System.out.println("loaded");

                Tools.postOnMainLoop(new Runnable() {

                    @Override
                    public void run() {
                        if (error != null) {
                            Tools.showErrorMessage(acitvity, "Rss Load Error");

                        } else {
                            acitvity.updateTableAdapter();
                        }
                    }
                });
            }
        });
    }

    public void onSnapshotChanged(TaskManager.TaskManagerSnapshot snapshot) {
        taskManagerView.showSnapshot(snapshot);
    }

    @Override
    public View getViewAtPosition(int pos) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos >= firstListItemPosition && pos <= lastListItemPosition ) {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }

        return null;
    }

    @Override
    public Range<Integer> getVisibleRange() {
        if (listView.getChildCount() == 0) {
            return Range.closed(0,0);
        }

        return Range.closed(listView.getFirstVisiblePosition(), listView.getFirstVisiblePosition() + listView.getChildCount() - 1);
    }

    void updateTableAdapter() {
        final ArrayList<RssItem> items = new ArrayList<RssItem>(this.feed.items());
        final RssItemsAdapter adapter = new RssItemsAdapter(this, items, this.taskManager);

        adapter.setListener(this);
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.rss_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
