package com.example.alexeyglushkov.wordteacher.main_module.router;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;

import com.example.alexeyglushkov.uimodulesandclasses.activitymodule.presenter.ActivityPresenter;
import com.example.alexeyglushkov.uimodulesandclasses.activitymodule.presenter.ActivityPresenterImp;
import com.example.alexeyglushkov.uimodulesandclasses.activitymodule.view.ActivityModuleView;
import com.example.alexeyglushkov.wordteacher.learningmodule.LearnActivity;
import com.example.alexeyglushkov.wordteacher.learningmodule.presenter.LearnPresenterImp;
import com.example.alexeyglushkov.wordteacher.learningmodule.LearnActivityFactory;
import com.example.alexeyglushkov.wordteacher.model.Card;

import java.util.List;

/**
 * Created by alexeyglushkov on 12.02.17.
 */

public class MainRouterImp implements MainRouter {

    public void showLearningModule(@NonNull Context context, @NonNull List<Card> cards) {
        startLearnActivity(context, cards);
    }

    private void startLearnActivity(@NonNull Context context, @NonNull List<Card> cards) {
        Activity activity = (Activity)context;

        Intent activityIntent = new Intent(activity, LearnActivity.class);
        String[] cardIds = new String[cards.size()];

        for (int i=0; i<cards.size(); ++i) {
            Card card = cards.get(i);
            cardIds[i] = card.getId().toString();
        }

        activityIntent.putExtra(ActivityModuleView.PRESENTER_CLASS_KEY, ActivityPresenterImp.class.getName());
        activityIntent.putExtra(ActivityPresenter.FACTORY_CLASS_KEY, LearnActivityFactory.class.getName());
        activityIntent.putExtra(LearnPresenterImp.EXTRA_CARD_IDS, cardIds);
        activityIntent.putExtra(LearnPresenterImp.EXTRA_DEFINITION_TO_TERM, true);

        activity.startActivityForResult(activityIntent, LearnPresenterImp.ACTIVITY_RESULT);
    }
}
