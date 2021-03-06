package com.example.alexeyglushkov.wordteacher.sessionresultmodule.presenter;

import android.os.Bundle;
import androidx.annotation.Nullable;

import com.example.alexeyglushkov.uimodulesandclasses.activitymodule.presenter.ActivityModuleItem;
import com.example.alexeyglushkov.uimodulesandclasses.activitymodule.view.ActivityModuleItemView;
import com.example.alexeyglushkov.wordteacher.listmodule.CompareStrategyFactory;
import com.example.alexeyglushkov.wordteacher.listmodule.StorableListProviderFactory;
import com.example.alexeyglushkov.wordteacher.listmodule.StorableResourceListLiveDataProvider;
import com.example.alexeyglushkov.wordteacher.listmodule.presenter.BaseListPresenter;
import com.example.alexeyglushkov.wordteacher.listmodule.view.ListViewInterface;
import com.example.alexeyglushkov.wordteacher.model.LearnSession;
import com.example.alexeyglushkov.wordteacher.sessionresultmodule.view.SessionResultAdapterView;

/**
 * Created by alexeyglushkov on 15.01.17.
 */

public class SessionResultPresenterImp extends BaseListPresenter<SessionResultAdapterView>
        implements SessionResultPresenter, ActivityModuleItem {

    @Override
    public void onCreated(Bundle savedInstanceState, Bundle extras) {
        super.onCreated(savedInstanceState, extras);

        if (savedInstanceState == null && extras != null) {
            LearnSession session = extras.getParcelable(SessionResultPresenter.EXTERNAL_SESSION);
            //provider = providerFactory.createFromObject(session);

        } else {
            //provider = providerFactory.restore(savedInstanceState);
        }
    }

    @Override
    public void onViewStateRestored(ListViewInterface view, @Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(view, savedInstanceState);
        reload();
    }

    @Override
    protected StorableResourceListLiveDataProvider<SessionResultAdapterView> createLiveDataProvider(Bundle bundle) {
        return null;
    }

    //    @Override
//    protected CompareStrategyFactory<SessionResultAdapterView> createCompareStrategyFactory() {
//        return new SessionResultCompareStrategyFactory();
//    }


    // ActivityModuleItem

    @Override
    public ActivityModuleItemView getActivityModuleItemView() {
        return view;
    }
}
