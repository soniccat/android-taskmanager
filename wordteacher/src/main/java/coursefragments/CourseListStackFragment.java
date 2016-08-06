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
public class CourseListStackFragment extends StackFragment {

    public static final String DEFAULT_TITLE = "Courses";

    private MainApplication getMainApplication() {
        return MainApplication.instance;
    }

    public CourseHolder getCourseHolder() {
        return getMainApplication().getCourseHolder();
    }

    private CourseListStackFragment.Listener getCourseListener() {
        return (CourseListStackFragment.Listener)listener;
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

    private void showCourseFragment() {
        CourseListFragment courseListFragment = new CourseListFragment();
        courseListFragment.setListener(getMenuCourseListener());

        addFragment(courseListFragment, null);
    }

    public void showCardListFragment(Course course) {
        CardListFragment fragment = new CardListFragment();
        fragment.setListener(getMenuCardsListener());

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
        CourseListFragment setFragment = getCourseFragment();
        if (setFragment != null) {
            setFragment.setListener(getMenuCourseListener());
        }

        CardListFragment cardFragment = getCardListFragment();
        if (cardFragment != null) {
            cardFragment.setListener(getMenuCourseListener());
        }
    }

    private CourseListFragment getCourseFragment() {
        List<Fragment> list = getChildFragmentManager().getFragments();
        return list != null && list.size() > 0 ? (CourseListFragment)list.get(0) : null;
    }

    private CardListFragment getCardListFragment() {
        List<Fragment> list = getChildFragmentManager().getFragments();
        return list != null && list.size() > 1 ? (CardListFragment)list.get(1) : null;
    }

    public void updateCourses() {
        CourseListFragment courseListFragment = getCourseFragment();
        if (courseListFragment != null) {
            applyPendingOperation(courseListFragment);
            ArrayList<Course> courses = getCourseHolder().getCourses();
            courseListFragment.setCourses(courses);
        }
    }

    public boolean hasCourses() {
        boolean result = false;
        CourseListFragment courseListFragment = getCourseFragment();
        if (courseListFragment != null) {
            result = courseListFragment.hasCourses();
        }

        return result;
    }

    public void applyPendingOperation(CourseListFragment fragment) {
        CourseListFragmentMenuListener listener = (CourseListFragmentMenuListener)fragment.getListener();
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

    private CourseListFragmentMenuListener getMenuCourseListener() {
        return new CourseListFragmentMenuListener(getContext(), getCourseHolder(), new CourseListFragmentMenuListener.Listener() {
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
                CourseListStackFragment.this.getCourseListener().onCourseClicked(course);
            }

            @Override
            public void onLearnNewWordsClick(Course course) {
                CourseListStackFragment.this.getCourseListener().onLearnNewWordsClick(course);
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

    private CardListFragmentMenuListener getMenuCardsListener() {
        return new CardListFragmentMenuListener(getContext(), getCourseHolder(), new CardListFragmentMenuListener.Listener() {
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