package com.example.rssreader;

import java.util.ArrayList;

import com.ga.image.Image;
import com.ga.image.ImageLoader;
import com.ga.loader.http.HttpLoadTask;
import com.ga.rss.RssFeed;
import com.ga.rss.RssItem;
import com.ga.task.PriorityTaskProvider;
import com.ga.task.SimpleTaskPool;
import com.ga.task.Task;
import com.ga.task.TaskManager;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class RssItemsAdapter extends ArrayAdapter<RssItem> implements Task.ProgressListener {

    /**
     *
     */
    private final Context context;
    private final ArrayList<RssItem> values;
    private final TaskManager taskManager;
    private RssItemsAdapterListener listener;
    private PriorityTaskProvider taskProvider;

    class ViewHolder {
        public TextView text;
        public ImageView imageView;
        public Image loadingImage;
        public int position;
        public ProgressBar progressBar;
    }

    public RssItemsAdapter(Context context, ArrayList<RssItem> values, TaskManager taskManager) {
        super(context, R.layout.feed_cell, values);
        this.context = context;
        this.values = values;
        this.taskManager = taskManager;

        Handler taskManagerHandler = this.taskManager.getHandler();
        this.taskProvider = new PriorityTaskProvider(taskManagerHandler, new SimpleTaskPool(taskManagerHandler));
        this.taskManager.addTaskProvider(this.taskProvider);
    }

    public RssItemsAdapterListener getListener() {
        return listener;
    }

    public void setListener(RssItemsAdapterListener listener) {
        this.listener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.feed_cell, parent, false);
            TextView textView = (TextView) rowView.findViewById(R.id.topLine);
            ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);

            ViewHolder holder = new ViewHolder();
            holder.text = textView;
            holder.imageView = imageView;

            holder.progressBar = (ProgressBar)rowView.findViewById(R.id.progress);
            holder.progressBar.setIndeterminate(false);

            convertView = rowView;
            convertView.setTag(holder);
        }

        final ViewHolder holder = (ViewHolder) convertView.getTag();

        RssItem item = values.get(position);
        holder.text.setText(item.title());
        holder.position = position;

        if (item.image() != null) {
            holder.progressBar.setVisibility(View.VISIBLE);
            loadImage(item.image(), holder);
        } else {
            holder.progressBar.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    void loadImage(final Image image, final ViewHolder holder) {

        holder.imageView.setImageBitmap(null);
        holder.loadingImage = image;
        if (image == null) {
            return;
        }

        final int position = holder.position;
        holder.progressBar.setProgress(0);

        //the position is used as a part of task id to handle the same images right
        Task task = ImageLoader.loadImage(null, image, Integer.toString(position), new ImageLoader.LoadCallback() {
            @Override
            public void completed(Task task, final Image image, final Bitmap bitmap, Error error) {
                View view = getListener().getViewAtPosition(position);
                if (view != null) {
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder.loadingImage == image) {
                        if (bitmap != null) {
                            holder.imageView.setImageBitmap(bitmap);
                        }
                        holder.loadingImage = null;
                        holder.progressBar.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });

        task.setTaskUserData(new Pair<Integer, Image>(position, image));

        //due to progresslisteners are stored through weakreference we can't create anonymous object
        task.addTaskProgressListener(this);
        task.setTaskProgressMinChange(0.2f);

        taskProvider.getTaskPool().addTask(task);
    }

    public void onTaskProgressChanged(Task task, float oldValue, float newValue) {
        assert task.getTaskUserData() instanceof Pair;

        Pair<Integer, Image> taskData = (Pair<Integer, Image>)task.getTaskUserData();

        View view = getListener().getViewAtPosition(taskData.first);
        if (view != null) {
            ViewHolder holder = (ViewHolder) view.getTag();
            if (holder.loadingImage == taskData.second) {
                holder.progressBar.setProgress((int)(newValue*100.0f));
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

        taskProvider.setUserData(firstVisibleItem);
        taskProvider.updatePriorities(new PriorityTaskProvider.PriorityProvider() {
            @Override
            public int getPriority(Task task) {
                assert task.getTaskUserData() instanceof Pair;

                Pair<Integer, Image> taskData = (Pair<Integer, Image>)task.getTaskUserData();
                int taskPosition = taskData.first;

                //for a test purpose we start load images from the center of the list view
                int delta = Math.abs(firstVisibleItem + visibleItemCount/2 - taskPosition);
                if (delta > 100) {
                    delta = 100;
                }

                return 100 - delta;
            }
        });
    }

    public interface RssItemsAdapterListener {
        View getViewAtPosition(int position);
    }
}
