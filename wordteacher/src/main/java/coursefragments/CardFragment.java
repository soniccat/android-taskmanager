package coursefragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.alexeyglushkov.wordteacher.R;

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
public class CardFragment extends BaseListFragment<Card> {
    public final static String ARG_PARENT_COURSE_ID = "PARENT_COURSE_ID";
    public final static String ARG_CARDS = "PARENT_COURSE_ID";

    private Course parentCourse;

    private MainApplication getMainApplication() {
        return MainApplication.instance;
    }

    public CourseHolder getCourseHolder() {
        return getMainApplication().getCourseHolder();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            parentCourse = savedInstanceState.getParcelable("parentCourse");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_quizlet_cards, container, false);
    }

    public void setParentCourse(Course parentCourse) {
        this.parentCourse = parentCourse;
    }

    public Course getParentCourse() {
        return parentCourse;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable("parentCourse", parentCourse);
        //saveAdapterState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        MainApplication.instance.addHolderListener(new MainApplication.CourseHolderListener() {
            @Override
            public void onLoaded() {
                onHolderLoaded();
            }
        });
    }

    private void onHolderLoaded() {
        Bundle bundle = getArguments();
        String parentCourseIdStr = bundle.getString(ARG_PARENT_COURSE_ID);
        if (parentCourseIdStr != null) {
            UUID courseId = UUID.fromString(bundle.getString(ARG_PARENT_COURSE_ID));
            parentCourse = getCourseHolder().getCourse(courseId);
            setCards(parentCourse.getCards());

        } else {

        }
    }

    /*
    private void saveAdapterState(Bundle outState) {
        Parcelable parcelable = getCardAdapter().onSaveInstanceState();
        outState.putParcelable("adapter", parcelable);
    }*/

    /*
    @Override
    protected void restoreAdapter(Bundle savedInstanceState) {
        Parcelable parcelable = savedInstanceState.getParcelable("adapter");
        getCardAdapter().onRestoreInstanceState(parcelable);
    }
    */

    public void setCards(List<Card> inCards) {
        ArrayList<Card> cards = new ArrayList<>();
        cards.addAll(inCards);

        sortCards(cards);
        getCardAdapter().updateCards(cards);
    }

    public void setCourses(ArrayList<Course> courses) {
        getCardAdapter().updateCards(sortCards(getCards(courses)));
    }

    public List<Card> getCards(List<Course> sets) {
        List<Card> cards = new ArrayList<>();
        for (Course set : sets) {
            cards.addAll(set.getCards());
        }

        return cards;
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

    private CardAdapter getCardAdapter() {
        return (CardAdapter)adapter;
    }

    public boolean hasCards() {
        List<Card> cards = getCards();
        int count = cards != null ? cards.size() : 0;
        return count > 0;
    }

    public ArrayList<Card> getCards() {
        ArrayList<Card> cards = getCardAdapter().getCards();
        return cards;
    }

    @Override
    protected BaseListAdaptor createAdapter() {
        return createCardAdapter();
    }

    private CardAdapter createCardAdapter() {
        CardAdapter adapter = new CardAdapter(new CardAdapter.Listener() {
            @Override
            public void onCardClicked(View view, Card card) {
                CardFragment.this.getListener().onRowClicked(card);
            }

            @Override
            public void onMenuClicked(View view, Card card) {
                CardFragment.this.getListener().onRowMenuClicked(card, view);
            }

            @Override
            public void onCardViewDeleted(View view, Card card) {
                CardFragment.this.getListener().onRowViewDeleted(card);
            }
        });

        return adapter;
    }
}
