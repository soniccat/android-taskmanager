package com.example.alexeyglushkov.uimodulesandclasses.pagermodule;

/**
 * Created by alexeyglushkov on 05.11.16.
 */

public interface PagerModule {
    void reload();
    void setCurrentIndex(int i);

    int getCurrentIndex();
    PagerModuleItem getModuleAtIndex(int i);

    void updatePage(int i); // to update title
}
