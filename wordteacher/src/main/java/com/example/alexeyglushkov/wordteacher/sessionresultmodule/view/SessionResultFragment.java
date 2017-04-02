package com.example.alexeyglushkov.wordteacher.sessionresultmodule.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.alexeyglushkov.wordteacher.R;

import com.example.alexeyglushkov.wordteacher.listmodule.view.BaseListAdaptor;
import com.example.alexeyglushkov.wordteacher.listmodule.view.BaseListFragment;
import com.example.alexeyglushkov.wordteacher.model.SessionCardResult;
import com.example.alexeyglushkov.wordteacher.sessionresultmodule.presenter.SessionResultPresenter;

/**
 * Created by alexeyglushkov on 15.05.16.
 */
public class SessionResultFragment extends BaseListFragment<SessionCardResult> {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_session_result, container, false);
    }

    @Override
    protected BaseListAdaptor createAdapter() {
        return new SessionResultAdapter();
    }

    @Override
    public SessionResultPresenter getPresenter() {
        return (SessionResultPresenter)super.getPresenter();
    }
}
