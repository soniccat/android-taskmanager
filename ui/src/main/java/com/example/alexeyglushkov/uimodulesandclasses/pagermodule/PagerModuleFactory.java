package com.example.alexeyglushkov.uimodulesandclasses.pagermodule;

/**
 * Created by alexeyglushkov on 05.11.16.
 */

public interface PagerModuleFactory {
    PagerModuleItem moduleAtIndex(int i, PagerModule stackModule);
    PagerModuleItem restoreModule(int i, Object viewObject, PagerModule stackModule);
}
