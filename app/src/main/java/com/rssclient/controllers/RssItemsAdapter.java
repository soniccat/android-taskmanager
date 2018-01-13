package com.rssclient.controllers;

import java.util.ArrayList;
import java.util.List;

import com.example.alexeyglushkov.streamlib.progress.ProgressListener;
import com.example.alexeyglushkov.taskmanager.image.Image;
import com.example.alexeyglushkov.taskmanager.image.ImageLoader;
import com.example.alexeyglushkov.streamlib.progress.ProgressInfo;
import com.example.alexeyglushkov.tools.HandlerTools;
import com.rssclient.model.RssItem;
import com.example.alexeyglushkov.taskmanager.task.*;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import junit.framework.Assert;

public class RssItemsAdapter extends ArrayAdapter<RssItem> {
    private final Context context;
    protected final ArrayList<RssItem> values;
    private RssItemsAdapterListener listener;

    class ViewHolder {
        public TextView text;
        public ImageView imageView;
        public Image loadingImage;
        public int position;
        public ProgressBar progressBar;
    }

    public RssItemsAdapter(Context context, ArrayList<RssItem> values) {
        super(context, R.layout.feed_cell, values);
        this.context = context;
        this.values = values;
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
            loadImage(convertView, item);
        } else {
            holder.progressBar.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    protected void loadImage(View convertView, RssItem item) {
        final ViewHolder holder = (ViewHolder) convertView.getTag();

        holder.imageView.setImageDrawable(null);
        holder.loadingImage = item.image();
        if (item.image() == null) {
            return;
        }

        final int position = holder.position;
        holder.progressBar.setProgress(0);

        getListener().loadImage(item);
    }

    public interface RssItemsAdapterListener {
        void loadImage(RssItem item);
    }
}
