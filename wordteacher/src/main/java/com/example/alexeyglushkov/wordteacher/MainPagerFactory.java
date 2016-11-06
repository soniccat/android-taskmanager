package com.example.alexeyglushkov.wordteacher;

import pagermodule.PagerModuleFactory;
import pagermodule.PagerModuleItem;
import stackmodule.StackModule;

/**
 * Created by alexeyglushkov on 05.11.16.
 */

public class MainPagerFactory implements PagerModuleFactory {
    @Override
    public PagerModuleItem moduleAtIndex(int i, StackModule stackModule) {
        return null;
    }

    @Override
    public PagerModuleItem restoreModule(int i, StackModule stackModule) {
        return null;
    }
}
