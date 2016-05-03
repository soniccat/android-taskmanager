package com.example.alexeyglushkov.wordteacher;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;

import com.example.alexeyglushkov.authorization.Auth.AccountStore;
import com.example.alexeyglushkov.authorization.service.Service;
import com.example.alexeyglushkov.quizletservice.QuizletService;
import com.example.alexeyglushkov.quizletservice.entities.QuizletSet;
import com.example.alexeyglushkov.quizletservice.entities.QuizletTerm;
import com.example.alexeyglushkov.service.SimpleService;
import com.example.alexeyglushkov.taskmanager.task.TaskManager;

import java.util.ArrayList;
import java.util.List;

import main.BaseActivity;
import main.MainApplication;

public class MainActivity extends BaseActivity implements QuizletCardsFragment.Listener {

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

        setOnViewReadyCallback();
    }

    private void setOnViewReadyCallback() {
        final View rootView = getWindow().getDecorView().getRootView();
        rootView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                rootView.getViewTreeObserver().removeOnPreDrawListener(this);
                onViewReady();
                return true;
            }
        });
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);


        if (fragment instanceof QuizletCardsFragment) {
            QuizletCardsFragment quizletFragment = (QuizletCardsFragment)fragment;
            quizletFragment.setListener(this);
        }
    }

    private void onViewReady() {
        if (getQuizletService() != null && getQuizletService().getAccount() != null && getQuizletService().getAccount().isAuthorized()) {
            loadQuizletSets();
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
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

    @Override
    public void onBackPressed() {
        // if there is a fragment and the back stack of this fragment is not empty,
        // then emulate 'onBackPressed' behaviour, because in default, it is not working
        FragmentManager fm = getSupportFragmentManager();
        for (Fragment frag : fm.getFragments()) {
            if (frag.isVisible()) {
                FragmentManager childFm = frag.getChildFragmentManager();
                if (childFm.getBackStackEntryCount() > 0) {
                    childFm.popBackStack();
                    return;
                }
            }
        }
        super.onBackPressed();
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
        getSetQuizletFragment().updateSets(sets);
        getCardQuizletFragment().updateSets(sets);
    }

    private QuizletCardsFragment getSetQuizletFragment() {
        return (QuizletCardsFragment)getFragment(0);
    }

    private QuizletCardsFragment getCardQuizletFragment() {
        return (QuizletCardsFragment)getFragment(1);
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

    // QuizletCardsFragment.Listener


    @Override
    public void onSetClicked(QuizletSet set) {
        showWordFragment(set);
    }

    private void showWordFragment(QuizletSet set) {
        QuizletCardsFragment fragment = new QuizletCardsFragment();
        fragment.setViewType(QuizletCardsFragment.ViewType.Cards);

        ArrayList<QuizletSet> list = new ArrayList<>();
        list.add(set);
        fragment.updateSets(list);

        //getSetQuizletFragment().getChildFragmentManager().beginTransaction().addToBackStack("showSet").replace(R.id.container,fragment).commitAllowingStateLoss();
        //getSupportFragmentManager().beginTransaction().addToBackStack("showSet").replace(R.id.pager,fragment).commitAllowingStateLoss();
    }

    @Override
    public void onSetMenuClicked(QuizletSet set) {

    }

    @Override
    public void onCardClicked(QuizletTerm card) {

    }

    @Override
    public void onTermMenuClicked(QuizletTerm card) {

    }
}
