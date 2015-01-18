package com.rssclient.controllers;

import java.io.Serializable;
import java.net.MalformedURLException;

import java.net.URL;
import java.util.ArrayList;

import com.main.MainApplication;
import com.rssclient.model.RssFeed;
import com.rssclient.model.RssStorage;
import com.ga.task.TaskManager;

import android.support.v7.app.ActionBarActivity;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

@SuppressLint("NewApi")
public class MainRssActivity extends ActionBarActivity implements FeedsAdapter.FeedsAdapterListener {
    public final static String FEED_OBJECT = "com.ga.mainActivity.FEED_OBJECT";

    TaskManager loader;

    //TODO: delete it
    TaskManager keeper;
    RssStorage rssStorage;

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MainApplication application = (MainApplication) getApplication();

        if (loader == null) {
            loader = application.loader();
        }

        if (keeper == null) {
            keeper = application.keeper();
        }

        if (rssStorage == null) {
            rssStorage = application.rssStorage();
        }

        setContentView(R.layout.activity_main_rss);

        final ListView listview = (ListView) findViewById(R.id.listview);
        this.listView = listview;
        final MainRssActivity activity = this;

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                activity.showFragmentActivityAtPos(position);
            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                final FeedsAdapter adapter = (FeedsAdapter)listview.getAdapter();
                if (adapter != null) {
                    adapter.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
                }
            }
        });


        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           final int position, long id) {

                activity.startActionMode(new ActionMode.Callback() {

                    @Override
                    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                        return false;
                    }

                    @Override
                    public void onDestroyActionMode(ActionMode mode) {
                    }

                    @Override
                    public boolean onCreateActionMode(ActionMode mode, Menu menu) {

                        MenuInflater inflater = mode.getMenuInflater();
                        inflater.inflate(R.menu.action_menu, menu);
                        return true;
                    }

                    @Override
                    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                        if (item.getItemId() == R.id.action_delete_feed) {
                            activity.deleteItemAtPos(position);
                            mode.finish();
                        }

                        return false;
                    }
                });
                return true;
            }

        });

        this.updateTableAdapter();
        this.loadRssStorage();
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

    public void showFragmentActivityAtPos(int pos) {
        RssFeed feed = this.rssStorage.feeds().get(pos);

        // Do something in response to button
        Intent intent = new Intent(this, RssItemsActivity.class);
        intent.putExtra(FEED_OBJECT, (Serializable) feed);

        startActivity(intent);
    }

    void showItemDialogAtPos(final int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        String[] items = {"Delete", "Cancel"};
        final MainRssActivity activity = this;

        builder.setTitle("Choose an action");
        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    activity.deleteItemAtPos(pos);
                }
            }
        });

        builder.create().show();
    }

    void loadFeedAtPos(int pos) {
        RssFeed feed = this.rssStorage.feeds().get(pos);
        this.rssStorage.loadFeed(this.loader, this, feed, new RssStorage.RssFeedCallback() {

            @Override
            public void completed(RssFeed feed, Error error) {
                System.out.println("feed loaded");
            }
        });
    }

    void updateTableAdapter() {
        final ListView listview = (ListView) findViewById(R.id.listview);
        final ArrayList<RssFeed> feeds = new ArrayList<RssFeed>(this.rssStorage.feeds());

        final FeedsAdapter adapter = new FeedsAdapter(this, feeds, this.loader);
        adapter.setListener(this);
        listview.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

        } else if (id == R.id.add_feed) {

            final MainRssActivity activity = this;
            this.showAlertDialog(new ObjectCompletion<String>() {

                @Override
                public void completed(String result) {
                    try {
                        URL url = new URL(result);
                        activity.addRssFeed(url);

                    } catch (MalformedURLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            });
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    void showAlertDialog(final ObjectCompletion<String> completion) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Type a feed url");

        final EditText textView = new EditText(this);
        textView.setText("http://www.lenta.ru/rss");
        builder.setView(textView);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.out.println("Ok pressed");
                String string = textView.getText().toString();
                completion.completed(string);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.out.println("Cancel pressed");
            }
        });

        Dialog dialog = builder.create();
        dialog.show();
    }

    void addRssFeed(URL url) {
        RssFeed feed = new RssFeed(url, url.toString());

        final MainRssActivity activity = this;
        this.rssStorage.loadFeed(this.loader, this, feed, new RssStorage.RssFeedCallback() {

            @Override
            public void completed(final RssFeed feed, final Error error) {
                if (error != null) {
                    Tools.showErrorMessage(activity, "Can't load rss");

                } else {
                    rssStorage.addFeed(feed);
                    activity.saveRssStorage();

                    FeedsAdapter adapter = (FeedsAdapter) activity.listView.getAdapter();
                    adapter.add(feed);
                }
            }
        });
    }

    void deleteItemAtPos(int pos) {
        RssFeed feed = this.rssStorage.feeds().get(pos);
        this.rssStorage.deleteFeed(feed);
        this.saveRssStorage();

        FeedsAdapter adapter = (FeedsAdapter) this.listView.getAdapter();
        adapter.remove(feed);
    }

    void handleFeedKeeped() {
    }

    void handleRssStorageLoad() {
        this.updateTableAdapter();
    }

    void loadRssStorage() {
        final MainRssActivity activity = this;
        rssStorage.load(loader, this, new RssStorage.RssStorageCallback() {

            @Override
            public void completed(final RssStorage storage, final Error error) {
                if (error != null) {
                    Tools.showErrorMessage(activity, "RssStore Load Error");

                } else {
                    int feedsCount = storage.feeds().size();
                    System.out.printf("loaded %d feeds\n", feedsCount);

                    activity.handleRssStorageLoad();
                }
            }
        });
    }

    void saveRssStorage() {
        final MainRssActivity activity = this;

        rssStorage.keep(keeper, this, new RssStorage.RssStorageCallback() {

            @Override
            public void completed(final RssStorage storage, final Error error) {
                if (error != null) {
                    Tools.showErrorMessage(activity, "RssStore Save Error");

                } else {
                    int feedsCount = storage.feeds().size();
                    System.out.printf("saved %d feeds\n", feedsCount);
                    activity.handleFeedKeeped();
                }
            }
        });
    }
}
