package com.example.alexeyglushkov.uimodulesandclasses.activitymodule.presenter;

import com.example.alexeyglushkov.uimodulesandclasses.activitymodule.view.ActivityModuleItemView;

/**
 * Created by alexeyglushkov on 02.04.17.
 */

public interface ActivityPresenterFactory {
    ActivityModuleItem createModule();
    ActivityModuleItem restore(ActivityModuleItemView view);
}
