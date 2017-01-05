package coursefragments;

import com.example.alexeyglushkov.quizletservice.entities.QuizletSet;

import coursefragments.cards.CardListFragment;
import coursefragments.cards.CardListPresenter;
import coursefragments.courses.CourseListFragment;
import coursefragments.courses.CourseListPresenter;
import listmodule.view.SimpleListFragment;
import listmodule.view.SimpleListFragmentListenerAdapter;
import main.Preferences;
import model.Card;
import model.Course;
import quizletfragments.sets.QuizletSetListFragment;
import quizletfragments.terms.QuizletTermListFragment;
import quizletfragments.terms.QuizletTermListPresenter;
import stackmodule.StackModule;
import stackmodule.StackModuleFactory;
import stackmodule.StackModuleItem;

/**
 * Created by alexeyglushkov on 04.01.17.
 */

public class CourseStackModuleFactory implements StackModuleFactory {
    private SimpleListFragment.Listener<Course> courseListListener;
    private SimpleListFragment.Listener<Card> cardListListener;

    @Override
    public StackModuleItem rootModule(final StackModule stackModule) {
        CourseListPresenter listPresenter = new CourseListPresenter();
        CourseListFragment fragment = CourseListFragment.create();
        bindSetListPresenterListener(listPresenter, stackModule);

        listPresenter.setView(fragment);
        fragment.setPresenter(listPresenter);

        listPresenter.setSortOrder(Preferences.getQuizletSetSortOrder());
        return listPresenter;
    }

    private void bindSetListPresenterListener(CourseListPresenter presenter, final StackModule stackModule) {
        presenter.setListener(new SimpleListFragmentListenerAdapter<Course>(createCourseListListenerProvider()) {
            @Override
            public void onRowClicked(Course data) {
                stackModule.push(data, null);
                super.onRowClicked(data);
            }
        });
    }

    @Override
    public StackModuleItem moduleFromObject(Object object, StackModule stackModule) {
        CardListPresenter listPresenter = new CardListPresenter();
        CardListFragment fragment = CardListFragment.create();
        bindTermListPresenterListener(listPresenter, stackModule);

        listPresenter.setView(fragment);
        fragment.setPresenter(listPresenter);
        listPresenter.setParentCourse((Course) object);

        listPresenter.setSortOrder(Preferences.getQuizletTermSortOrder());
        return listPresenter;
    }

    private void bindTermListPresenterListener(CardListPresenter presenter, StackModule stackModule) {
        presenter.setListener(new SimpleListFragmentListenerAdapter<Card>(createCardListListenerProvider()));
    }

    @Override
    public StackModuleItem restoreModule(Object viewObject, StackModule stackModule) {
        StackModuleItem item = null;
        if (viewObject instanceof QuizletSetListFragment) {
            CourseListFragment setListFragment = (CourseListFragment)viewObject;
            CourseListPresenter setListPresenter = (CourseListPresenter)setListFragment.getPresenter();
            bindSetListPresenterListener(setListPresenter, stackModule);
            item = setListPresenter;

        } else if (viewObject instanceof QuizletTermListFragment) {
            CardListFragment termListFragment = (CardListFragment)viewObject;
            CardListPresenter termListPresenter = (CardListPresenter)termListFragment.getPresenter();
            bindTermListPresenterListener(termListPresenter, stackModule);
            item = termListPresenter;
        }

        return item;
    }

    //// Creation methods

    private SimpleListFragmentListenerAdapter.ListenerProvider<Course> createCourseListListenerProvider() {
        // we return provider because listener is null at the moment of restoration and will be set later
        return new SimpleListFragmentListenerAdapter.ListenerProvider<Course>() {
            @Override
            public SimpleListFragment.Listener<Course> getListener() {
                return courseListListener;
            }
        };
    }

    private SimpleListFragmentListenerAdapter.ListenerProvider<Card> createCardListListenerProvider() {
        // we return provider because listener is null at the moment of restoration and will be set later
        return new SimpleListFragmentListenerAdapter.ListenerProvider<Card>() {
            @Override
            public SimpleListFragment.Listener<Card> getListener() {
                return cardListListener;
            }
        };
    }

    //// Setters

    public void setCourseListListener(SimpleListFragment.Listener<Course> cardListener) {
        this.courseListListener = cardListener;
    }

    public void setCardListListener(SimpleListFragment.Listener<Card> cardListener) {
        this.cardListListener = cardListener;
    }
}
