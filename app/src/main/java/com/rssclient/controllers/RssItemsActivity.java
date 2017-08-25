package com.rssclient.controllers;

import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.example.alexeyglushkov.streamlib.progress.ProgressInfo;
import com.example.alexeyglushkov.streamlib.progress.ProgressListener;
import com.example.alexeyglushkov.taskmanager.image.Image;
import com.example.alexeyglushkov.taskmanager.image.ImageLoader;
import com.example.alexeyglushkov.taskmanager.loader.http.HttpLoadTask;
import com.example.alexeyglushkov.taskmanager.task.PriorityTaskProvider;
import com.example.alexeyglushkov.taskmanager.task.RestorableTaskProvider;
import com.example.alexeyglushkov.taskmanager.task.SimpleTaskManagerSnapshot;
import com.example.alexeyglushkov.taskmanager.task.Task;
import com.example.alexeyglushkov.taskmanager.task.TaskManagerSnapshot;
import com.example.alexeyglushkov.tools.HandlerTools;
import com.main.MainApplication;
import com.rssclient.model.RssFeed;
import com.rssclient.model.RssStorage;
import com.rssclient.model.RssItem;
import com.example.alexeyglushkov.taskmanager.task.TaskManager;
import com.example.alexeyglushkov.taskmanager.ui.TaskManagerView;
import com.google.common.collect.Range;

import android.graphics.Bitmap;
import android.support.annotation.IntegerRes;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import junit.framework.Assert;


public class RssItemsActivity extends ActionBarActivity implements RssItemsAdapter.RssItemsAdapterListener, TaskManagerSnapshot.OnSnapshotChangedListener, ProgressListener {

    final static public String PROVIDER_ID = "providerID";
    final static public String FEED_URL = "feedURL";

    TaskManager taskManager;
    private RestorableTaskProvider taskProvider;

    ListView listView;
    RssStorage rssStorage;
    TaskManagerView taskManagerView;

    TaskManagerSnapshot snapshot;
    RssFeed feed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rss_items);

        MainApplication application = (MainApplication) getApplication();

        Intent intent = getIntent();

        rssStorage = application.getRssStorage();

        String urlString;
        if (savedInstanceState == null) {
            urlString = (String) intent.getSerializableExtra(FEED_URL);
        } else {
            urlString = savedInstanceState.getString(FEED_URL);
        }

        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        feed = rssStorage.getFeed(url);

        taskManager = application.getTaskManager();
        this.taskProvider = (RestorableTaskProvider) taskManager.getTaskProvider(PROVIDER_ID);

        boolean needRestoreImages = false;
        if (this.taskProvider == null) {
            this.taskProvider = new RestorableTaskProvider(new PriorityTaskProvider(this.taskManager.getHandler(), PROVIDER_ID));
            this.taskManager.addTaskProvider(this.taskProvider);
            
        } else {
            needRestoreImages = true;
            // TODO: figure out when it's better to call that to not lose completed and active tasks
        }
        
        taskManagerView = (TaskManagerView) findViewById(R.id.task_manager_view);

        snapshot = new SimpleTaskManagerSnapshot();
        snapshot.startSnapshotRecording(taskManager);
        snapshot.addSnapshotListener(this);

        listView = (ListView) this.findViewById(R.id.listview);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                final RssItemsAdapter adapter = (RssItemsAdapter)view.getAdapter();
                if (adapter != null) {
                    RssItemsActivity.this.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
                }
            }
        });

        final RssItemsActivity acitvity = this;

        if (feed.items() == null || feed.items().size() == 0) {
            rssStorage.loadFeed(taskManager, this, feed, new RssStorage.RssFeedCallback() {

                @Override
                public void completed(RssFeed feed, final Error error) {
                    // TODO Auto-generated method stub
                    System.out.println("loaded");

                    HandlerTools.runOnMainThread(new Runnable() {
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
        } else {
            acitvity.updateTableAdapter();

            if (needRestoreImages) {
                taskProvider.restoreTaskCompletions(new RestorableTaskProvider.TaskCompletionProvider() {
                    @Override
                    public Task.Callback getCallback(Task task) {
                        HttpLoadTask httpTask = (HttpLoadTask)task;
                        if (httpTask.getTaskUserData() instanceof Pair) {
                            Pair<Integer, Image> taskData = (Pair<Integer, Image>) task.getTaskUserData();
                            RssItem item = feed.items().get(taskData.first);

                            ImageLoader.LoadCallback imageLoadCallback = getLoadImageCallback(item, RssItemsActivity.this);
                            return ImageLoader.getTaskCallback(httpTask, taskData.second, imageLoadCallback);
                        }

                        return null;
                    }
                });
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        taskProvider.setRecording(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //taskProvider.setRecording(false);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(FEED_URL, feed.getURL().toString());
    }

    public void onSnapshotChanged(TaskManagerSnapshot snapshot) {
        taskManagerView.showSnapshot(snapshot);
    }

    public View getViewAtPosition(int pos) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos >= firstListItemPosition && pos <= lastListItemPosition ) {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }

        return null;
    }

    public Range<Integer> getVisibleRange() {
        if (listView.getChildCount() == 0) {
            return Range.closed(0,0);
        }

        return Range.closed(listView.getFirstVisiblePosition(), listView.getFirstVisiblePosition() + listView.getChildCount() - 1);
    }

    void updateTableAdapter() {
        final ArrayList<RssItem> items = new ArrayList<RssItem>(this.feed.items());
        final RssItemsAdapter adapter = new RssItemsAdapter(this, items);

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

    //

    @Override
    public void loadImage(RssItem item) {
        RssItemsAdapter adapter = (RssItemsAdapter)listView.getAdapter();

        int position = adapter.values.indexOf(item);
        if (position == -1) {
            return;
        }

        Image image = item.image();

        //the position is used as a part of task id to handle the same images right
        Task task = ImageLoader.loadImage(null, image, Integer.toString(position), getLoadImageCallback(item, this));

        Range<Integer> range = getVisibleRange();
        task.setTaskType(position%2 + 1);
        taskManager.setLimit(1, 0.5f);
        taskManager.setLimit(2, 0.5f);
        task.setTaskPriority(getTaskPriority(position, range.lowerEndpoint(), range.upperEndpoint() - range.lowerEndpoint() + 1));
        task.setTaskUserData(new Pair<Integer, Image>(position, image));

        task.addTaskProgressListener(this);
        task.setTaskProgressMinChange(0.2f);
        task.setLoadPolicy(Task.LoadPolicy.SkipIfAdded);

        taskProvider.addTask(task);
    }

    @NonNull
    public static ImageLoader.LoadCallback getLoadImageCallback(final RssItem item, RssItemsActivity activity) {
        final WeakReference<RssItemsActivity> ref = new WeakReference<RssItemsActivity>(activity);

        return new ImageLoader.LoadCallback() {
            @Override
            public void completed(Task task, final Image image, final Bitmap bitmap, Error error) {
                RssItemsActivity act = ref.get();
                if (act == null || act.isDestroyed() || act.isFinishing()) {
                    return;
                }

                RssItemsAdapter adapter = (RssItemsAdapter)act.listView.getAdapter();
                if (adapter == null) {
                    return;
                }

                int position = adapter.values.indexOf(item);
                if (position == -1) {
                    return;
                }

                View view = act.getViewAtPosition(position);
                if (view != null) {
                    // TODO: move holder access to adapter
                    RssItemsAdapter.ViewHolder holder = (RssItemsAdapter.ViewHolder) view.getTag();
                    if (holder.loadingImage == image) {
                        if (bitmap != null) {
                            holder.imageView.setImageBitmap(bitmap);
                        }
                        holder.loadingImage = null;
                        holder.progressBar.setVisibility(View.INVISIBLE);
                    }
                }
            }
        };
    }

    public void onProgressChanged(Object sender, ProgressInfo info) {
        Task task = (Task)sender;
        Assert.assertTrue(task.getTaskUserData() instanceof Pair);

        Pair<Integer, Image> taskData = (Pair<Integer, Image>)task.getTaskUserData();

        View view = getViewAtPosition(taskData.first);
        if (view != null) {
            // TODO: move holder access to adapter
            RssItemsAdapter.ViewHolder holder = (RssItemsAdapter.ViewHolder) view.getTag();
            if (holder.loadingImage == taskData.second) {
                holder.progressBar.setProgress((int)(info.getNormalizedValue()*100.0f));
                //Log.d("imageprogress","progress " + newValue);
            } else {
                //Log.d("imageprogress","loadingImage is different");
            }
        }
    }

    public void onScroll(AbsListView view, final int firstVisibleItem, final int visibleItemCount, int totalItemCount) {
        if (taskProvider.getUserData() instanceof Integer) {
            int distance = Math.abs((Integer)taskProvider.getUserData() - firstVisibleItem);
            if (distance < 5) {
                return;
            }
        }

        //TODO: it should be done via api without direct access to getStreamReader
        HandlerTools.runOnHandlerThread(taskProvider.getHandler(), new Runnable() {
            @Override
            public void run() {
                List<Task> tasks = new ArrayList<Task>();
                tasks.addAll(taskProvider.getTasks());

                for (Task t : tasks) {
                    Pair<Integer, Image> taskData = (Pair<Integer, Image>)t.getTaskUserData();
                    int distance = Math.abs((Integer)taskProvider.getUserData() - taskData.first);
                    if (distance > 30) {
                        taskManager.cancel(t,null);
                    }
                }
            }
        });

        PriorityTaskProvider priorityTaskProvider = (PriorityTaskProvider)taskProvider.getProvider();

        taskProvider.setUserData(firstVisibleItem);
        priorityTaskProvider.updatePriorities(new PriorityTaskProvider.PriorityProvider() {
            @Override
            public int getPriority(Task task) {
                Assert.assertTrue(task.getTaskUserData() instanceof Pair);

                Pair<Integer, Image> taskData = (Pair<Integer, Image>)task.getTaskUserData();
                int taskPosition = taskData.first;
                return getTaskPriority(taskPosition, firstVisibleItem, visibleItemCount);
            }
        });
    }

    private int getTaskPriority(int taskPosition, int firstVisibleItem, int visibleItemCount) {
        //for a test purpose we start load images from the center of the list view
        int delta = Math.abs(firstVisibleItem + visibleItemCount/2 - taskPosition);
        if (delta > 100) {
            delta = 100;
        }

        return 100 - delta;
    }
}
