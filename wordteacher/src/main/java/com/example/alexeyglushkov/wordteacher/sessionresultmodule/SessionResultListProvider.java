package com.example.alexeyglushkov.wordteacher.sessionresultmodule;

import android.os.Bundle;

import com.example.alexeyglushkov.wordteacher.listmodule.SimpleListProvider;
import com.example.alexeyglushkov.wordteacher.listmodule.SimpleStorableListProvider;
import com.example.alexeyglushkov.wordteacher.model.SessionCardResult;

import java.util.List;

/**
 * Created by alexeyglushkov on 15.01.17.
 */

public class SessionResultListProvider extends SimpleStorableListProvider<SessionCardResult> {
    public SessionResultListProvider(List<SessionCardResult> items) {
        super(items);
    }

    @Override
    public void store(Bundle bundle) {

    }

    @Override
    public void restore(Bundle bundle, Object context) {

    }
}
