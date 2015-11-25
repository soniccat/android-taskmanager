package com.main;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.authorization.AuthActivityProxy;
import com.authorization.AuthorizationActivity;
import com.example.alexeyglushkov.authorization.Auth.Account;
import com.example.alexeyglushkov.authorization.Auth.AuthCredentials;
import com.example.alexeyglushkov.authorization.Auth.Authorizer;
import com.example.alexeyglushkov.authorization.OAuth.OAuth20Authorizer;
import com.example.alexeyglushkov.taskmanager.task.SimpleTask;
import com.example.alexeyglushkov.taskmanager.task.Task;
import com.example.alexeyglushkov.taskmanager.task.TaskManager;
import com.playground.PlaygroundActivity;
import com.rssclient.controllers.MainRssActivity;
import com.rssclient.controllers.R;

public class MainActivity extends ActionBarActivity {
    private static final String TAG = "MainActivity";

    private MainApplication getMainApplication() {
        return (MainApplication)getApplication();
    }

    public TaskManager getTaskManager() {
        return getMainApplication().getTaskManager();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = (ListView)findViewById(R.id.list);
        listView.setAdapter(new ArrayAdapter<String>(this,android.R.layout.activity_list_item, android.R.id.text1, new String[]{"Rss Client", "Playground", "Authorization"}));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    showRssClient();
                } else if (position == 1) {
                    showPlayground();
                } else if (position == 2) {
                    showAuthorization();
                }
            }
        });
    }

    private void showRssClient() {
        Intent intent = new Intent(this, MainRssActivity.class);
        startActivity(intent);
    }

    private void showPlayground() {
        Intent intent = new Intent(this, PlaygroundActivity.class);
        startActivity(intent);
    }

    private void showAuthorization() {
        AuthActivityProxy.setCurrentActivity(this);

        Task authTask = new SimpleTask() {
            @Override
            public void startTask() {
                final Account account = Networks.createAccount(Networks.Network.Foursquare);
                final OAuth20Authorizer authorizer = (OAuth20Authorizer)account.getAuthorizer();
                authorizer.setWebClient(new AuthActivityProxy());

                account.authorize(new Authorizer.AuthorizerCompletion() {
                    @Override
                    public void onFinished(AuthCredentials credentials, Error error) {
                        Log.d(TAG, "showAuthorization onFinished " + account.getCredentials().isValid());
                        getPrivate().handleTaskCompletion();
                    }
                });
            }
        };

        getTaskManager().addTask(authTask);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
