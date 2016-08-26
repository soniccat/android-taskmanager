package coursefragments.cards;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.alexeyglushkov.wordteacher.R;

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

    //// Creation, initialization, restoration

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_quizlet_cards, container, false);
    }

    @Override
    public void onViewStateRestored(@Nullable final Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        MainApplication.instance.addCourseHolderListener(new MainApplication.ReadyListener() {
            @Override
            public void onReady() {
                onHolderLoaded(savedInstanceState);
            }
        });
    }

    //// Events

    private void onHolderLoaded(Bundle savedInstanceState) {
        providerFactory = createFactory();
        restoreProviderIfNeeded(savedInstanceState);
        reload();
    }

    //// Actions

    //// Creation Methods

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

    private void createFactoryIfNeeded() {
        if (providerFactory == null) {
            providerFactory = createFactory();
        }
    }

    @NonNull
    private CardListFactory createFactory() {
        return new CardListFactory(getCourseHolder());
    }

    //// Setters

    // Data Setters

    public void setParentCourse(Course course) {
        createFactoryIfNeeded();
        provider = providerFactory.createFromObject(course);
    }

    //// Getters

    // App Getters

    private MainApplication getMainApplication() {
        return MainApplication.instance;
    }

    public CourseHolder getCourseHolder() {
        return getMainApplication().getCourseHolder();
    }

    // Data Getters

    public Course getParentCourse() {
        Course result = null;
        if (provider instanceof CourseCardListProvider) {
            CourseCardListProvider courseProvider = (CourseCardListProvider)provider;
            result = courseProvider.getCourse();
        }

        return result;
    }

    // Cast Getters

    private CardListAdapter getCardAdapter() {
        return (CardListAdapter)adapter;
    }
}
