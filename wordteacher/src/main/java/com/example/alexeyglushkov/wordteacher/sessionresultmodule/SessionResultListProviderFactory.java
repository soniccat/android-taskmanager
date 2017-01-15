package com.example.alexeyglushkov.wordteacher.sessionresultmodule;

import android.os.Bundle;

import com.example.alexeyglushkov.wordteacher.listmodule.StorableListProvider;
import com.example.alexeyglushkov.wordteacher.listmodule.StorableListProviderFactory;
import com.example.alexeyglushkov.wordteacher.model.SessionCardResult;

import junit.framework.Assert;

import java.util.List;

/**
 * Created by alexeyglushkov on 15.01.17.
 */

public class SessionResultListProviderFactory implements StorableListProviderFactory<SessionCardResult> {

    @Override
    public StorableListProvider<SessionCardResult> createFromList(List<SessionCardResult> list) {
        Assert.fail("Not supported");
        return null;
    }

    @Override
    public StorableListProvider<SessionCardResult> createFromObject(Object obj) {
        return null;
    }

    @Override
    public StorableListProvider<SessionCardResult> restore(Bundle bundle) {
        return null;
    }

    @Override
    public StorableListProvider<SessionCardResult> createDefault() {
        Assert.fail("Not supported");
        return null;
    }
}
