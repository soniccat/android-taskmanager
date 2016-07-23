package com.example.alexeyglushkov.wordteacher;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.alexeyglushkov.quizletservice.entities.QuizletSet;
import com.example.alexeyglushkov.quizletservice.entities.QuizletTerm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import model.Card;
import model.Course;

/**
 * Created by alexeyglushkov on 08.05.16.
 */
public class CourseFragment extends BaseListFragment<Course> {

    // think about dividing into 2 fragments with base
    /*enum ViewType {
        Courses,
        Cards
    }*/

    //private Course parentCourse;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_quizlet_cards, container, false);
    }

    /*
    public void deleteCardView(Card card) {
        CardAdapter adapter = getCardAdapter();
        int index = adapter.getCardIndex(card);
        if (index != -1) {
            View view = getCourseView(index);
            if (view != null) {
                int position = recyclerView.getChildLayoutPosition(view);
                adapter.deleteCardAtIndex(index);
                adapter.notifyItemRemoved(position);
            }
        }
    }*/

    /*
    public void deleteCourseView(Course course) {
        CourseAdapter adapter = getCourseAdapter();
        int index = adapter.getCourseIndex(course);
        if (index != -1) {
            View view = getCourseView(index);
            if (view != null) {
                int position = recyclerView.getChildLayoutPosition(view);
                adapter.deleteCourseAtIndex(index);
                adapter.notifyItemRemoved(position);
            }
        }
    }*/

    protected void restore(@Nullable Bundle savedInstanceState) {
        //parentCourse = savedInstanceState.getParcelable("parentCourse");
    }

    protected void restoreAdapter(@Nullable Bundle savedInstanceState) {
        Parcelable parcelable = savedInstanceState.getParcelable("adapter");
        getCourseAdapter().onRestoreInstanceState(parcelable);

        /*
        if (viewType == ViewType.Courses) {

        } else {
            getCardAdapter().onRestoreInstanceState(parcelable);
        }
        */
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //outState.putParcelable("parentCourse", parentCourse);
        saveAdapterState(outState);
    }

    private void saveAdapterState(Bundle outState) {
        Parcelable parcelable;
        /*if (viewType == ViewType.Courses) {
            parcelable = getCourseAdapter().onSaveInstanceState();
        } else {
            parcelable = getCardAdapter().onSaveInstanceState();
        }*/
        parcelable = getCourseAdapter().onSaveInstanceState();

        outState.putParcelable("adapter", parcelable);
    }

    public void setCourses(ArrayList<Course> courses) {
        getCourseAdapter().setCourses(sortCourses(courses));
        /*if (viewType == ViewType.Courses) {
            getCourseAdapter().setCourses(sortCourses(courses));
        } else {
            getCardAdapter().updateCards(sortCards(getCards(courses)));
        }*/
    }



    public boolean hasCourses() {
        List<Course> courses = getCourses();
        int count = courses != null ? courses.size() : 0;
        return count > 0;
    }

    public List<Course> getCourses() {
        return getCourseAdapter().getCourses();
    }

    /*
    public boolean hasCards() {
        List<Card> cards = getCards();
        int count = cards != null ? cards.size() : 0;
        return count > 0;
    }

    public ArrayList<Card> getCards() {
        ArrayList<Card> cards = null;
        if (viewType == ViewType.Cards) {
            cards = getCardAdapter().getCards();
        }

        return cards;
    }*/

    /*
    public void setParentCourse(Course parentCourse) {
        this.parentCourse = parentCourse;
    }

    public Course getParentCourse() {
        return parentCourse;
    }

    public void setCards(List<Card> inCards) {
        setViewType(ViewType.Cards);

        ArrayList<Card> cards = new ArrayList<>();
        cards.addAll(inCards);

        sortCards(cards);
        getCardAdapter().updateCards(cards);
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
    }*/

    private ArrayList<Course> sortCourses(ArrayList<Course> courses) {
        Collections.sort(courses, new Comparator<Course>() {
            @Override
            public int compare(Course lhs, Course rhs) {
                return rhs.getCreateDate().compareTo(lhs.getCreateDate());
            }
        });

        return courses;
    }

    /*
    public void setViewType(ViewType aViewType) {
        if (viewType != aViewType) {
            viewType = aViewType;
            recreateAdapter();
        }
    }

    public ViewType getViewType() {
        return viewType;
    }*/

    private CourseAdapter getCourseAdapter() {
        return (CourseAdapter)adapter;
    }

    /*
    private CardAdapter getCardAdapter() {
        return (CardAdapter)adapter;
    }*/

    @Override
    protected BaseListAdaptor createAdapter() {
        return createCourseAdapter();
    }

    /*
    private void recreateAdapter() {
        if (viewType == ViewType.Courses) {
            adapter = createCourseAdapter();
        } else {
            adapter = createCardAdapter();
        }

        if (recyclerView != null) {
            applyAdapter();
        }
    }*/

    private CourseAdapter createCourseAdapter() {
        CourseAdapter adapter = new CourseAdapter(new CourseAdapter.Listener() {
            @Override
            public void onCourseClicked(View view, Course course) {
                CourseFragment.this.getListener().onRowClicked(course);
            }

            @Override
            public void onCourseMenuClicked(View view, Course course) {
                CourseFragment.this.getListener().onRowMenuClicked(course, view);
            }

            @Override
            public void onCourseViewDeleted(View view, Course course) {
                CourseFragment.this.getListener().onRowViewDeleted(course);
            }
        });

        return adapter;
    }

    /*
    private CardAdapter createCardAdapter() {
        CardAdapter adapter = new CardAdapter(new CardAdapter.Listener() {
            @Override
            public void onCardClicked(View view, Card card) {
                CourseFragment.this.onCardClicked(view, card);
            }

            @Override
            public void onMenuClicked(View view, Card card) {
                CourseFragment.this.onCardMenuClicked(view, card);
            }

            @Override
            public void onCardViewDeleted(View view, Card card) {
                CourseFragment.this.onCardViewDeleted(card);
            }
        });

        return adapter;
    }*/

    /*
    private void onCourseClicked(View v, Course course) {
        listener.onCourseClicked(course);
    }

    private void onCourseMenuClicked(View v, Course course) {
        listener.onCourseMenuClicked(course, v);
    }

    private void onCourseViewDeleted(Course course) {
        listener.onCourseViewDeleted(course);
    }

    private void onCardClicked(View v, Card card) {
        listener.onCardClicked(card);
    }

    private void onCardMenuClicked(View v, Card card) {
        listener.onCardMenuClicked(card, v);
    }

    private void onCardViewDeleted(Card card) {
        listener.onCardViewDeleted(card);
    }*/
}
