package com.example.alexeyglushkov.wordteacher.courselistmodules.cardlistmodule.presenter;

import android.os.Bundle;

import junit.framework.Assert;

import java.util.List;

import com.example.alexeyglushkov.wordteacher.listmodule.StorableListProvider;
import com.example.alexeyglushkov.wordteacher.listmodule.StorableListProviderFactory;
import com.example.alexeyglushkov.wordteacher.model.Card;
import com.example.alexeyglushkov.wordteacher.model.Course;
import com.example.alexeyglushkov.wordteacher.model.CourseHolder;

/**
 * Created by alexeyglushkov on 21.08.16.
 */
public class CardListProviderFactory implements StorableListProviderFactory<Card> {
    private CourseHolder holder;

    //// Initialization
    public CardListProviderFactory(CourseHolder holder) {
        Assert.assertNotNull(holder);
        this.holder = holder;
    }

    //// Interface methods

    @Override
    public StorableListProvider<Card> createFromList(List<Card> list) {
        return new CardListProvider(list);
    }

    @Override
    public StorableListProvider<Card> createFromObject(Object obj) {
        StorableListProvider<Card> result = null;

        if (obj instanceof Course) {
            result = new CourseCardListProvider((Course)obj);

        } else {
            Assert.fail("Unknown object");
        }

        return result;
    }

    @Override
    public StorableListProvider<Card> restore(Bundle bundle) {
        StorableListProvider<Card> result = null;

        if (CourseCardListProvider.canRestore(bundle)) {
            result = new CourseCardListProvider(bundle, holder);

        } else if (CardListProvider.canRestore(bundle)) {
            result = new CardListProvider(bundle, holder);

        } else {
            result = createDefault();
        }

        return result;
    }

    @Override
    public StorableListProvider<Card> createDefault() {
        Assert.fail("Not supported");
        return null;
    }
}
