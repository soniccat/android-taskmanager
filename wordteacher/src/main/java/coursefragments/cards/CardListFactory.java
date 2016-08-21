package coursefragments.cards;

import android.os.Bundle;

import junit.framework.Assert;

import java.util.List;

import listfragment.StorableListProvider;
import listfragment.StorableListProviderFactory;
import model.Card;
import model.Course;
import model.CourseHolder;

/**
 * Created by alexeyglushkov on 21.08.16.
 */
public class CardListFactory implements StorableListProviderFactory<Card> {
    private CourseHolder holder;

    //// Initialization
    public CardListFactory(CourseHolder holder) {
        Assert.assertNotNull(holder);
        this.holder = holder;
    }

    //// Interface methods

    @Override
    public StorableListProvider<Card> createFromList(List<Card> list) {
        Assert.fail("Not supported");
        return null;
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
        return null;
    }

    @Override
    public StorableListProvider<Card> createDefault() {
        Assert.fail("Not supported");
        return null;
    }
}
