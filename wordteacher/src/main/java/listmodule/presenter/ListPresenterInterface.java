package listmodule.presenter;

import android.os.Bundle;

import listmodule.ListModuleInterface;
import listmodule.view.ListViewInterface;

/**
 * Created by alexeyglushkov on 30.10.16.
 */

public interface ListPresenterInterface extends ListModuleInterface {
    void initialize();
    void store(Bundle bundle);

    void onCreated(Bundle savedInstanceState);
    void onViewCreated(Bundle savedInstanceState);
    void onViewStateRestored(ListViewInterface view, Bundle savedInstanceState);

    void onResume();
    void onPause();
    void onDestroyView();
    void onDestroy();
}
