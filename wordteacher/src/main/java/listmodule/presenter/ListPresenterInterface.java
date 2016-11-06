package listmodule.presenter;

import android.os.Bundle;

/**
 * Created by alexeyglushkov on 30.10.16.
 */

public interface ListPresenterInterface {
    void initialize();
    void store(Bundle bundle);

    void onViewCreated(Bundle savedInstanceState);
    void onViewStateRestored(Bundle savedInstanceState);

    void onResume();
    void onPause();
    void onDestroyView();
    void onDestroy();
}
