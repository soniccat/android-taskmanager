package com.example.alexeyglushkov.wordteacher.sessionresultmodule;

import android.os.Bundle;

import com.example.alexeyglushkov.wordteacher.listmodule.StorableListProvider;
import com.example.alexeyglushkov.wordteacher.listmodule.StorableListProviderFactory;
import com.example.alexeyglushkov.wordteacher.model.LearnSession;
import com.example.alexeyglushkov.wordteacher.model.SessionCardResult;
import com.example.alexeyglushkov.wordteacher.sessionresultmodule.view.SessionResultAdapterView;

import junit.framework.Assert;

import java.util.List;

/**
 * Created by alexeyglushkov on 15.01.17.
 */

public class SessionResultListProviderFactory implements StorableListProviderFactory<SessionResultAdapterView> {
    private LearnSession session;

    public SessionResultListProviderFactory(LearnSession s) {
        session = s;
    }

    @Override
    public StorableListProvider<SessionResultAdapterView> createFromList(List<SessionResultAdapterView> list) {
        return new SessionResultListProvider(list);
    }

    @Override
    public StorableListProvider<SessionResultAdapterView> createFromObject(Object obj) {
        Assert.fail("Not supported");
        return null;
    }

    @Override
    public StorableListProvider<SessionResultAdapterView> restore(Bundle bundle) {
        SessionResultListProvider provider = new SessionResultListProvider(null);
        return provider;
    }

    @Override
    public StorableListProvider<SessionResultAdapterView> createDefault() {
        Assert.fail("Not supported");
        return null;
    }
}
