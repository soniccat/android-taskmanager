package com.example.alexeyglushkov.wordteacher.sessionresultmodule.presenter;

import android.os.Bundle;

import com.example.alexeyglushkov.wordteacher.listmodule.StorableListProvider;
import com.example.alexeyglushkov.wordteacher.listmodule.StorableListProviderFactory;
import com.example.alexeyglushkov.wordteacher.model.LearnSession;
import com.example.alexeyglushkov.wordteacher.sessionresultmodule.view.SessionResultAdapterView;

import org.junit.Assert;

import java.util.List;

/**
 * Created by alexeyglushkov on 15.01.17.
 */

public class SessionResultListProviderFactory implements StorableListProviderFactory<SessionResultAdapterView> {
    public SessionResultListProviderFactory() {
    }

    @Override
    public StorableListProvider<SessionResultAdapterView> createFromList(List<SessionResultAdapterView> list) {
        Assert.fail("Not supported");
        return null;
    }

    @Override
    public StorableListProvider<SessionResultAdapterView> createFromObject(Object obj) {
        LearnSession session = (LearnSession)obj;
        return new SessionResultListProvider(session);
    }

    @Override
    public StorableListProvider<SessionResultAdapterView> restore(Bundle bundle) {
        return new SessionResultListProvider(bundle);
    }

    @Override
    public StorableListProvider<SessionResultAdapterView> createDefault() {
        Assert.fail("Not supported");
        return null;
    }
}
