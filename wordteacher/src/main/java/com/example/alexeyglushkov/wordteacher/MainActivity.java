package com.example.alexeyglushkov.wordteacher;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.example.alexeyglushkov.authorization.Auth.AccountStore;
import com.example.alexeyglushkov.authorization.service.Service;
import com.example.alexeyglushkov.quizletservice.QuizletService;
import com.example.alexeyglushkov.quizletservice.entities.QuizletSet;
import com.example.alexeyglushkov.service.SimpleService;
import com.example.alexeyglushkov.taskmanager.task.TaskManager;

import java.util.ArrayList;
import java.util.List;

import authorization.AuthorizationActivity;
import main.BaseActivity;
import main.MainApplication;
import main.Networks;

public class MainActivity extends BaseActivity {

    private ViewPager pager;

    private MainApplication getMainApplication() {
        return (MainApplication)getApplication();
    }

    public TaskManager getTaskManager() {
        return getMainApplication().getTaskManager();
    }

    public AccountStore getAccountStore() {
        return getMainApplication().getAccountStore();
    }

    //// Lifecycle

    public QuizletService getQuizletService() {
        return getMainApplication().getQuizletService();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pager = (ViewPager)findViewById(R.id.pager);

        MainPageAdapter adapter = new MainPageAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(pager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFabPressed();
            }
        });
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

    //// Other

    private void onFabPressed() {
        loadQuizletSets();
    }

    private void loadQuizletSets() {
        getQuizletService().loadSets(new SimpleService.CommandCallback() {
            @Override
            public void onCompleted(Error error) {
                onQuizletSetsLoaded(error);
            }
        });
    }

    private void onQuizletSetsLoaded(Error error) {
        if (error != null) {
            showErrorSnackBar(error);
        } else {
            handleLoadedQuizletSets();
        }
    }

    private void handleLoadedQuizletSets() {
        List<QuizletSet> sets = getQuizletService().getSets();
        getQuizletFragment().setSets(sets);
    }

    private QuizletCardsFragment getQuizletFragment() {
        return (QuizletCardsFragment)getFragment(0);
    }

    private Fragment getFragment(int i) {
        return (Fragment)pager.getAdapter().instantiateItem(pager, i);
    }

    private void showErrorSnackBar(Error error) {
        String errorString = "";
        if (error instanceof Service.AuthError) {
            errorString = "Auth Error";
        } else {
            errorString = "Load Error";
        }

        Snackbar.make(pager.getChildAt(pager.getCurrentItem()), errorString, Snackbar.LENGTH_LONG).show();
    }
}
