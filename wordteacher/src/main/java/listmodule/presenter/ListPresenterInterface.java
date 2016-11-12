package listmodule.presenter;

import android.os.Bundle;

import listmodule.view.ListViewInterface;

/**
 * Created by alexeyglushkov on 30.10.16.
 */

public interface ListPresenterInterface {
    void initialize();
    void store(Bundle bundle);

    void onViewCreated(Bundle savedInstanceState);
    void onViewStateRestored(ListViewInterface view, Bundle savedInstanceState);

    void onResume();
    void onPause();
    void onDestroyView();
    void onDestroy();
}