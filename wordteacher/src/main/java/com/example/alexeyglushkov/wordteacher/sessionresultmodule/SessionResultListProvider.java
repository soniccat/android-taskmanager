package com.example.alexeyglushkov.wordteacher.sessionresultmodule;

import android.os.Bundle;

import com.example.alexeyglushkov.wordteacher.listmodule.SimpleListProvider;
import com.example.alexeyglushkov.wordteacher.listmodule.SimpleStorableListProvider;
import com.example.alexeyglushkov.wordteacher.model.SessionCardResult;
import com.example.alexeyglushkov.wordteacher.sessionresultmodule.view.SessionResultAdapterView;

import java.util.List;

/**
 * Created by alexeyglushkov on 15.01.17.
 */

public class SessionResultListProvider extends SimpleStorableListProvider<SessionResultAdapterView> {


    public SessionResultListProvider(List<SessionResultAdapterView> items) {
        super(items);
    }

    @Override
    public void store(Bundle bundle) {

    }

    @Override
    public void restore(Bundle bundle, Object context) {

    }
}
