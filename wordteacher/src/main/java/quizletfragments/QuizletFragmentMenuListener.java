package quizletfragments;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.example.alexeyglushkov.quizletservice.entities.QuizletSet;
import com.example.alexeyglushkov.quizletservice.entities.QuizletTerm;

import java.util.ArrayList;

import listfragment.ListMenuListener;
import model.Card;
import model.Course;
import model.CourseHolder;

/**
 * Created by alexeyglushkov on 14.08.16.
 */
public abstract class QuizletFragmentMenuListener<T> extends ListMenuListener<T> {
    protected CourseHolder courseHolder;

    public QuizletFragmentMenuListener(Context context, Listener listener, CourseHolder courseHolder) {
        super(context, listener);
        this.courseHolder = courseHolder;
    }

    protected CourseHolder getCourseHolder() {
        return courseHolder;
    }

    protected void createCourse(String title, ArrayList<Card> cards) {
        Course course = new Course();
        course.setTitle(title);
        course.addCards(cards);

        Error error = getCourseHolder().addCourse(course);
        if (error != null) {
            getListener().onCourseCreated(course);
        }
    }

    @NonNull
    protected Card createCard(QuizletTerm term) {
        Card card = new Card();
        card.setTerm(term.getTerm());
        card.setDefinition(term.getDefinition());
        card.setQuizletTerm(term);
        return card;
    }

    public Listener getListener() {
        return (Listener)this.listener;
    }

    public interface Listener<T> extends ListMenuListener.Listener<T> {
        void onCourseCreated(Course course);
        void onCourseChanged(Course course);
        void onCardsAdded(Course course);
        ViewGroup getDialogContainer();
    }
}
