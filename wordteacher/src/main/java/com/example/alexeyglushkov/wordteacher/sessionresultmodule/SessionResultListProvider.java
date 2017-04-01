package com.example.alexeyglushkov.wordteacher.sessionresultmodule;

import android.os.Bundle;
import android.os.Parcelable;

import com.example.alexeyglushkov.wordteacher.listmodule.SimpleListProvider;
import com.example.alexeyglushkov.wordteacher.listmodule.SimpleStorableListProvider;
import com.example.alexeyglushkov.wordteacher.model.LearnSession;
import com.example.alexeyglushkov.wordteacher.model.SessionCardResult;
import com.example.alexeyglushkov.wordteacher.sessionresultmodule.view.SessionResultAdapterView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by alexeyglushkov on 15.01.17.
 */

public class SessionResultListProvider extends SimpleStorableListProvider<SessionResultAdapterView> {

    public SessionResultListProvider(LearnSession session) {
        super();

        List<SessionResultAdapterView> result = new ArrayList<>();
        for(int i = 0; i < session.getResults().size(); ++i) {
            SessionResultAdapterView view = new SessionResultAdapterView();
            view.card = session.getCard(i);
            view.result = session.getCardResult(i);

            result.add(view);
        }

        setItems(result);
    }

    public SessionResultListProvider(Bundle bundle) {
        super();
        restore(bundle, null);
    }

    @Override
    public void store(Bundle bundle) {
        SessionResultAdapterView array[] = new SessionResultAdapterView[items.size()];
        items.toArray(array);

        bundle.putParcelableArray("list", array);
    }

    @Override
    public void restore(Bundle bundle, Object context) {
        Parcelable array[] = bundle.getParcelableArray("list");
        SessionResultAdapterView[] adapterViews = new SessionResultAdapterView[array.length];
        for(int i = 0; i < array.length; ++i) {
            adapterViews[i] = (SessionResultAdapterView)array[i];
        }

        List<SessionResultAdapterView> list = new ArrayList<>(Arrays.asList(adapterViews));
        setItems(list);
    }
}
