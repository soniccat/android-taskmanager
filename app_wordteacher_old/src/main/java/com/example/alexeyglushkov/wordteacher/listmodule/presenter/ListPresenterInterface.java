package com.example.alexeyglushkov.wordteacher.listmodule.presenter;

import android.os.Bundle;

import com.example.alexeyglushkov.wordteacher.listmodule.ListModuleInterface;
import com.example.alexeyglushkov.wordteacher.listmodule.view.ListViewInterface;

/**
 * Created by alexeyglushkov on 30.10.16.
 */

public interface ListPresenterInterface extends ListModuleInterface {
    void initialize();
    void store(Bundle bundle);

    void onCreated(Bundle savedInstanceState, Bundle extras);
    void onViewCreated(ListViewInterface view, Bundle savedInstanceState);
    void onViewStateRestored(ListViewInterface view, Bundle savedInstanceState);

    void onResume();
    void onPause();
    void onDestroyView();
    void onDestroy();
}
