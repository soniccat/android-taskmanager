package coursefragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.alexeyglushkov.wordteacher.R;

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import listfragment.BaseListAdaptor;
import listfragment.BaseListFragment;
import main.MainApplication;
import model.Card;
import model.Course;
import model.CourseHolder;

/**
 * Created by alexeyglushkov on 30.07.16.
 */
public class CardListFragment extends BaseListFragment<Card> {
    public final static String ARG_PARENT_COURSE_ID = "ARG_PARENT_COURSE_ID";
    //public final static String ARG_CARD_IDS = "ARG_CARD_IDS"; // TODO: add arbitrary cards support via another provider

    private CardListProvider provider;

    private MainApplication getMainApplication() {
        return MainApplication.instance;
    }

    public CourseHolder getCourseHolder() {
        return getMainApplication().getCourseHolder();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_quizlet_cards, container, false);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        MainApplication.instance.addCourseHolderListener(new MainApplication.ReadyListener() {
            @Override
            public void onReady() {
                onHolderLoaded();
            }
        });
    }

    private void onHolderLoaded() {
        Bundle bundle = getArguments();
        String parentCourseIdStr = bundle.getString(ARG_PARENT_COURSE_ID);
        UUID courseId = UUID.fromString(parentCourseIdStr);
        Assert.assertNotNull("now only CourserCardsProvider is supported", courseId);

        if (parentCourseIdStr != null) {
            provider = createCourseProvider(courseId);
        }

        reload();
    }

    private CourseCardListProvider createCourseProvider(UUID courseId) {
        Course parentCourse = getCourseHolder().getCourse(courseId);
        CourseCardListProvider result = new CourseCardListProvider(parentCourse);
        return result;
    }

    public Course getParentCourse() {
        Course result = null;
        if (provider instanceof CourseCardListProvider) {
            CourseCardListProvider courseProvider = (CourseCardListProvider)provider;
            result = courseProvider.getCourse();
        }

        return result;
    }

    public void reload() {
        setAdapterCards(getCards());
    }

    private void setAdapterCards(List<Card> inCards) {
        ArrayList<Card> cards = new ArrayList<>();
        cards.addAll(inCards);

        sortCards(cards);
        getCardAdapter().setCards(cards);
    }

    private List<Card> sortCards(List<Card> cards) {
        Collections.sort(cards, new Comparator<Card>() {
            @Override
            public int compare(Card lhs, Card rhs) {
                return lhs.getTerm().compareToIgnoreCase(rhs.getTerm());
            }
        });

        return cards;
    }

    private CardListAdapter getCardAdapter() {
        return (CardListAdapter)adapter;
    }

    public boolean hasCards() {
        List<Card> cards = getCards();
        int count = cards != null ? cards.size() : 0;
        return count > 0;
    }

    private List<Card> getCards() {
        return provider != null ? provider.getCards() : null;
    }

    @Override
    protected BaseListAdaptor createAdapter() {
        return createCardAdapter();
    }

    private CardListAdapter createCardAdapter() {
        CardListAdapter adapter = new CardListAdapter(new CardListAdapter.Listener() {
            @Override
            public void onCardClicked(View view, Card card) {
                CardListFragment.this.getListener().onRowClicked(card);
            }

            @Override
            public void onMenuClicked(View view, Card card) {
                CardListFragment.this.getListener().onRowMenuClicked(card, view);
            }

            @Override
            public void onCardViewDeleted(View view, Card card) {
                CardListFragment.this.getListener().onRowViewDeleted(card);
            }
        });

        return adapter;
    }
}
