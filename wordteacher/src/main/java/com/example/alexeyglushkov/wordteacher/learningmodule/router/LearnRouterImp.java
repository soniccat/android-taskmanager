package com.example.alexeyglushkov.wordteacher.learningmodule.router;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.example.alexeyglushkov.wordteacher.model.LearnSession;
import com.example.alexeyglushkov.wordteacher.sessionresultmodule.SessionResultActivity;
import com.example.alexeyglushkov.wordteacher.sessionresultmodule.presenter.SessionResultPresenter;

/**
 * Created by alexeyglushkov on 02.04.17.
 */

public class LearnRouterImp implements LearnRouter {
    @Override
    public void showResultModule(@NonNull Context context, LearnSession session) {
        Intent intent = new Intent(context, SessionResultActivity.class);
        intent.putExtra(SessionResultPresenter.EXTERNAL_SESSION, session);

        Activity activity = (Activity)context;
        activity.startActivityForResult(intent, SessionResultActivity.ACTIVITY_RESULT);
    }
}
