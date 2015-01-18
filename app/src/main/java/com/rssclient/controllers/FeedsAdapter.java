package com.rssclient.controllers;

import java.util.ArrayList;

import com.ga.image.Image;
import com.ga.image.ImageLoader;
import com.rssclient.model.RssFeed;
import com.ga.task.Task;
import com.ga.task.TaskManager;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class FeedsAdapter extends ArrayAdapter<RssFeed> {

    /**
     *
     */
    private final Context context;
    private final ArrayList<RssFeed> values;
    private final TaskManager taskManager;
    private SparseArray<Image> loadingImages;
    private FeedsAdapterListener listener;

    class ViewHolder {
        public TextView text;
        public ImageView imageView;

        public Image loadingImage;
        public int position;
    }

    public FeedsAdapter(Context context, ArrayList<RssFeed> values, TaskManager taskManager) {
        super(context, R.layout.feed_cell, values);
        this.context = context;
        this.values = values;
        this.taskManager = taskManager;
        this.loadingImages = new SparseArray<Image>();
    }

    public FeedsAdapterListener getListener() {
        return listener;
    }

    public void setListener(FeedsAdapterListener listener) {
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

            convertView = rowView;
            convertView.setTag(holder);
        }

        final ViewHolder holder = (ViewHolder) convertView.getTag();

        RssFeed feed = (RssFeed) values.get(position);
        holder.text.setText(feed.name());
        holder.position = position;

        loadImage(feed.image(), holder);

        return convertView;
    }

    void loadImage(Image image, final ViewHolder holder) {
        holder.imageView.setImageBitmap(null);
        holder.loadingImage = image;
        if (image == null) {
            return;
        }

        final int position = holder.position;

        ImageLoader.loadImage(this.taskManager, image, Integer.toString(holder.hashCode()), new ImageLoader.LoadCallback() {

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
                        //TODO: hide progress bar
                    }
                }
            }
        });
    }

    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    public interface FeedsAdapterListener {
        View getViewAtPosition(int position);
    }
}