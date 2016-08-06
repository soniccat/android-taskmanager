package coursefragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.example.alexeyglushkov.wordteacher.StackFragment;

import java.util.ArrayList;
import java.util.List;

import main.MainApplication;
import model.Card;
import model.Course;
import model.CourseHolder;

/**
 * Created by alexeyglushkov on 12.06.16.
 */
public class CourseStackFragment extends StackFragment {

    public static final String DEFAULT_TITLE = "Courses";

    private MainApplication getMainApplication() {
        return MainApplication.instance;
    }

    public CourseHolder getCourseHolder() {
        return getMainApplication().getCourseHolder();
    }

    private CourseStackFragment.Listener getCourseListener() {
        return (CourseStackFragment.Listener)listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState == null) {
            showCourseFragment();
        } else {
            restoreListeners();
        }
    }

    public void updateCourses(ArrayList<Course> courses) {
        CourseFragment courseFragment = getCourseFragment();
        courseFragment.setCourses(courses);
    }

    private void showCourseFragment() {
        CourseFragment courseFragment = new CourseFragment();
        courseFragment.setListener(getMenuCourseListener());

        addFragment(courseFragment, null);
    }

    public void showCardListFragment(Course course) {
        CardListFragment fragment = new CardListFragment();
        fragment.setListener(getMenuCardsListener());

        //ArrayList<Course> list = new ArrayList<>();
        //list.add(course);

        Bundle arg = new Bundle();
        arg.putString(CardListFragment.ARG_PARENT_COURSE_ID, course.getId().toString());
        fragment.setArguments(arg);

        addFragment(fragment, new TransactionCallback() {
            @Override
            public void onFinished() {
            }
        });
    }

    private void restoreListeners() {
        CourseFragment setFragment = getCourseFragment();
        if (setFragment != null) {
            setFragment.setListener(getMenuCourseListener());
        }

        CardListFragment cardFragment = getCardListFragment();
        if (cardFragment != null) {
            cardFragment.setListener(getMenuCourseListener());
        }
    }

    private CourseFragment getCourseFragment() {
        List<Fragment> list = getChildFragmentManager().getFragments();
        return list != null && list.size() > 0 ? (CourseFragment)list.get(0) : null;
    }

    private CardListFragment getCardListFragment() {
        List<Fragment> list = getChildFragmentManager().getFragments();
        return list != null && list.size() > 1 ? (CardListFragment)list.get(1) : null;
    }

    public void updateCourses() {
        CourseFragment courseFragment = getCourseFragment();
        if (courseFragment != null) {
            applyPendingOperation(courseFragment);
            ArrayList<Course> courses = getCourseHolder().getCourses();
            courseFragment.setCourses(courses);
        }
    }

    public void applyPendingOperation(CourseFragment fragment) {
        CourseFragmentMenuListener listener = (CourseFragmentMenuListener)fragment.getListener();
        listener.applyPendingOperation();
    }

    public void updateCards() {
        CardListFragment cardFragment = getCardListFragment();
        if (cardFragment != null) {
            cardFragment.reload();
        }
    }

    public String getTitle() {
        String title = null;
        if (getBackStackSize() > 0) {
            Course course = getCardListFragment().getParentCourse();
            if (course != null) {
                title = course.getTitle();
            }
        } else {
            title = DEFAULT_TITLE;
        }

        return title;
    }

    private CourseFragmentMenuListener getMenuCourseListener() {
        return new CourseFragmentMenuListener(getContext(), getCourseHolder(), new CourseFragmentMenuListener.Listener() {
            @Override
            public void onCourseDeleteClicked(Course course) {
                getCourseFragment().deleteView(course);
            }

            @Override
            public void onShowCourseContentClicked(Course course) {
                showCardListFragment(course);
            }

            @Override
            public void onRowClicked(Course course) {
                CourseStackFragment.this.getCourseListener().onCourseClicked(course);
            }

            @Override
            public void onLearnNewWordsClick(Course course) {
                CourseStackFragment.this.getCourseListener().onLearnNewWordsClick(course);
            }

            @Override
            public void onDataDeletionCancelled(Course course) {
                updateCourses();
            }

            @Override
            public void onDataDeleted(Course course) {
            }

            @Override
            public View getSnackBarViewContainer() {
                return getView();
            }
        });
    }

    private CardFragmentMenuListener getMenuCardsListener() {
        return new CardFragmentMenuListener(getContext(), getCourseHolder(), new CardFragmentMenuListener.Listener() {
            @Override
            public void onCardDeleteClicked(Card data) {
                getCardListFragment().deleteView(data);
            }

            @Override
            public void onRowClicked(Card data) {

            }

            @Override
            public void onDataDeletionCancelled(Card data) {
                updateCards();
            }

            @Override
            public void onDataDeleted(Card data) {
                if (getBackStackSize() == 0) {
                    updateCourses();
                }
            }

            @Override
            public View getSnackBarViewContainer() {
                return getView();
            }
        });
    }

    // CourseFragment.Listener

    public interface Listener extends StackFragment.Listener {
        void onCourseClicked(Course course);
        void onLearnNewWordsClick(Course course);
    }
}
