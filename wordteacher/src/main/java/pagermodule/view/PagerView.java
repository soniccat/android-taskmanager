package pagermodule.view;

import android.os.Bundle;

/**
 * Created by alexeyglushkov on 05.11.16.
 */

public interface PagerView {
    void setItemCount(int itemCount);
    void updateView(int index);

    void onViewCreated(Bundle savedInstanceState);
    void onSaveInstanceState(Bundle outState);
    void onRestoreInstanceState(Bundle savedInstanceState);
}
