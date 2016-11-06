package pagermodule;

import stackmodule.StackModuleItem;
import stackmodule.view.StackView;

/**
 * Created by alexeyglushkov on 05.11.16.
 */

public interface PagerModule {
    void reload();
    void setCurrentIndex(int i);

    int getCurrentIndex();
    PagerModuleItem getModuleAtIndex(int i);
}
