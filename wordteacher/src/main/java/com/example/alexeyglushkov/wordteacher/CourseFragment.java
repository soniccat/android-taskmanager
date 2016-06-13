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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import model.Card;
import model.Course;

/**
 * Created by alexeyglushkov on 08.05.16.
 */
public class CourseFragment extends Fragment {

    enum ViewType {
        Courses,
        Cards
    }

    private Course parentCourse;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private ViewType viewType = ViewType.Courses;
    private Listener listener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            restore(savedInstanceState);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_quizlet_cards, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        if (adapter == null) {
            recreateAdapter();
        }

        applyAdapter();
    }

    public void deleteCard(Card card) {
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
    }

    public void deleteCourse(Course course) {
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
    }

    private View getCourseView(int index) {
        RecyclerView.ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(index);
        View view = holder.itemView;

        return view;
    }

    private void restore(@Nullable Bundle savedInstanceState) {
        int intViewType = savedInstanceState.getInt("viewType");
        viewType = ViewType.values()[intViewType];
        parentCourse = savedInstanceState.getParcelable("parentCourse");
        recreateAdapter();
        restoreAdapter(savedInstanceState);
    }

    private void restoreAdapter(@Nullable Bundle savedInstanceState) {
        Parcelable parcelable = savedInstanceState.getParcelable("adapter");
        if (viewType == ViewType.Courses) {
            getCourseAdapter().onRestoreInstanceState(parcelable);
        } else {
            getCardAdapter().onRestoreInstanceState(parcelable);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("viewType", viewType.ordinal());
        outState.putParcelable("parentCourse", parentCourse);
        saveAdapterState(outState);
    }

    private void saveAdapterState(Bundle outState) {
        Parcelable parcelable;
        if (viewType == ViewType.Courses) {
            parcelable = getCourseAdapter().onSaveInstanceState();
        } else {
            parcelable = getCardAdapter().onSaveInstanceState();
        }

        outState.putParcelable("adapter", parcelable);
    }

    public void setCourses(ArrayList<Course> courses) {
        if (viewType == ViewType.Courses) {
            getCourseAdapter().setCourses(courses);
        } else {
            getCardAdapter().updateCards(getCards(courses));
        }
    }

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

        sortCards(cards);
        return cards;
    }

    private void sortCards(List<Card> cards) {
        Collections.sort(cards, new Comparator<Card>() {
            @Override
            public int compare(Card lhs, Card rhs) {
                return lhs.getTerm().compareTo(rhs.getTerm());
            }
        });
    }

    public void setViewType(ViewType aViewType) {
        if (viewType != aViewType) {
            viewType = aViewType;
            recreateAdapter();
        }
    }

    public ViewType getViewType() {
        return viewType;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    private CourseAdapter getCourseAdapter() {
        return (CourseAdapter)adapter;
    }

    private CardAdapter getCardAdapter() {
        return (CardAdapter)adapter;
    }

    private void recreateAdapter() {
        if (viewType == ViewType.Courses) {
            adapter = createCourseAdapter();
        } else {
            adapter = createCardAdapter();
        }

        if (recyclerView != null) {
            applyAdapter();
        }
    }

    private void applyAdapter() {
        recyclerView.setAdapter(adapter);
    }

    private CourseAdapter createCourseAdapter() {
        CourseAdapter adapter = new CourseAdapter(new CourseAdapter.Listener() {
            @Override
            public void onCourseClicked(View view, Course course) {
                CourseFragment.this.onCourseClicked(view, course);
            }

            @Override
            public void onCourseMenuClicked(View view, Course course) {
                CourseFragment.this.onCourseMenuClicked(view, course);
            }

            @Override
            public boolean onCourseDeleted(View view, Course course) {
                return CourseFragment.this.onCourseDeleted(course);
            }
        });

        return adapter;
    }

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
            public boolean onCardDeleted(View view, Card card) {
                return CourseFragment.this.onCardDeleted(card);
            }
        });

        return adapter;
    }

    private void onCourseClicked(View v, Course course) {
        listener.onCourseClicked(course);
    }

    private void onCourseMenuClicked(View v, Course course) {
        listener.onCourseMenuClicked(course, v);
    }

    private boolean onCourseDeleted(Course course) {
        return listener.onCourseDeleted(course);
    }

    private void onCardClicked(View v, Card card) {
        listener.onCardClicked(card);
    }

    private void onCardMenuClicked(View v, Card card) {
        listener.onCardMenuClicked(card, v);
    }

    private boolean onCardDeleted(Card card) {
        return listener.onCardDeleted(card);
    }

    public interface Listener {
        void onCourseClicked(Course course);
        void onCourseMenuClicked(Course course, View view);
        boolean onCourseDeleted(Course course);
        void onCardClicked(Card card);
        void onCardMenuClicked(Card card, View view);
        boolean onCardDeleted(Card card);
    }
}
