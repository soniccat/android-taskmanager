package com.example.alexeyglushkov.wordteacher.learningmodule.router;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.example.alexeyglushkov.uimodulesandclasses.activitymodule.presenter.ActivityPresenter;
import com.example.alexeyglushkov.uimodulesandclasses.activitymodule.presenter.ActivityPresenterImp;
import com.example.alexeyglushkov.uimodulesandclasses.activitymodule.view.ActivityModuleView;
import com.example.alexeyglushkov.wordteacher.model.LearnSession;
import com.example.alexeyglushkov.wordteacher.sessionresultmodule.SessionResultActivity;
import com.example.alexeyglushkov.wordteacher.sessionresultmodule.SessionResultActivityFactory;
import com.example.alexeyglushkov.wordteacher.sessionresultmodule.presenter.SessionResultPresenter;

/**
 * Created by alexeyglushkov on 02.04.17.
 */

public class LearnRouterImp implements LearnRouter {
    @Override
    public void showResultModule(@NonNull Context context, LearnSession session) {
        Intent intent = new Intent(context, SessionResultActivity.class);
        intent.putExtra(ActivityModuleView.PRESENTER_CLASS_KEY, ActivityPresenterImp.class.getName());
        intent.putExtra(ActivityPresenter.FACTORY_CLASS_KEY, SessionResultActivityFactory.class.getName());
        intent.putExtra(SessionResultPresenter.EXTERNAL_SESSION, session);

        Activity activity = (Activity)context;
        activity.startActivityForResult(intent, SessionResultActivity.ACTIVITY_RESULT);
    }
}
