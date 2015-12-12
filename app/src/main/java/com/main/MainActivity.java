package com.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.alexeyglushkov.authorization.Auth.Account;
import com.example.alexeyglushkov.authorization.Auth.AccountStore;
import com.example.alexeyglushkov.authorization.Auth.AuthCredentials;
import com.example.alexeyglushkov.authorization.Auth.Authorizer;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommand;
import com.example.alexeyglushkov.authorization.requestbuilder.HttpUrlConnectionBuilder;
import com.example.alexeyglushkov.authtaskmanager.ServiceTask;
import com.example.alexeyglushkov.authtaskmanager.ServiceTaskProvider;
import com.example.alexeyglushkov.authtaskmanager.ServiceTaskRunner;
import com.example.alexeyglushkov.cachemanager.CacheProvider;
import com.example.alexeyglushkov.cachemanager.DiskCacheProvider;
import com.example.alexeyglushkov.service.SimpleService;
import com.example.alexeyglushkov.taskmanager.task.SimpleTask;
import com.example.alexeyglushkov.taskmanager.task.Task;
import com.example.alexeyglushkov.taskmanager.task.TaskManager;
import com.playground.PlaygroundActivity;
import com.rssclient.controllers.MainRssActivity;
import com.rssclient.controllers.R;

import java.io.File;
import java.util.List;

public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";
    private SimpleService service;
    private CacheProvider cacheProvider;

    private MainApplication getMainApplication() {
        return (MainApplication)getApplication();
    }

    public TaskManager getTaskManager() {
        return getMainApplication().getTaskManager();
    }

    public AccountStore getAccountStore() {
        return getMainApplication().getAccountStore();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = (ListView)findViewById(R.id.list);
        listView.setAdapter(new ArrayAdapter<String>(this,android.R.layout.activity_list_item, android.R.id.text1, new String[]{"Rss Client", "Playground", "Authorization", "Run Request", "Clear cache"}));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    showRssClient();
                } else if (position == 1) {
                    showPlayground();
                } else if (position == 2) {
                    showAuthorization();
                } else if (position == 3) {
                    requestUser();
                } else if (position == 4) {
                    clearCache();
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
        Task authTask = new SimpleTask() {
            @Override
            public void startTask() {
                final Account account = Networks.createAccount(Networks.Network.Foursquare);
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

    private void requestUser() {
        if (service == null) {
            initService();
        }

        HttpUrlConnectionBuilder builder = new HttpUrlConnectionBuilder();
        builder.setUrl("https://api.foursquare.com/v2/users/self?v=20140806&m=foursquare");

        final ServiceTask cmd = new ServiceTask();
        cmd.setCache(cacheProvider);
        cmd.setConnectionBuilder(builder);
        cmd.setServiceCommandCallback(new ServiceCommand.Callback() {
            @Override
            public void onCompleted() {
                Log.d(TAG, "finished " + cmd.getResponse());
            }
        });

        service.runCommand(cmd, true);
    }

    private void initService() {
        service = new SimpleService();

        List<Account> accounts = getAccountStore().getAccounts(Networks.Network.Foursquare.ordinal());
        Account serviceAccount = null;
        if (accounts.size() > 0) {
            serviceAccount = accounts.get(0);
        } else {
            serviceAccount = Networks.createAccount(Networks.Network.Foursquare);
        }

        service.setAccount(serviceAccount);

        // TODO: we need the solution for id
        service.setServiceCommandProvider(new ServiceTaskProvider());
        service.setServiceCommandRunner(new ServiceTaskRunner(getTaskManager(), "31234"));

        cacheProvider = getServiceCache();
    }

    private CacheProvider getServiceCache() {
        File cacheDir = getDir("ServiceCache", MODE_PRIVATE);
        return new DiskCacheProvider(cacheDir);
    }

    private void clearCache() {
        getAccountStore().removeAll();
        getServiceCache().removeAll();
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
