package quizletlistmodules;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import com.example.alexeyglushkov.quizletservice.entities.QuizletTerm;
import com.example.alexeyglushkov.wordteacher.R;

import java.util.ArrayList;
import java.util.List;

import listmodule.ListMenuListener;
import model.Card;
import model.Course;
import model.CourseHolder;

/**
 * Created by alexeyglushkov on 14.08.16.
 */
public abstract class QuizletPresenterMenuListener<T> extends ListMenuListener<T> {
    private @NonNull CourseHolder courseHolder;

    public QuizletPresenterMenuListener(@NonNull Context context, @NonNull Listener<T> listener, @NonNull CourseHolder courseHolder) {
        super(context, listener);
        this.courseHolder = courseHolder;
    }

    @NonNull
    protected CourseHolder getCourseHolder() {
        return courseHolder;
    }

    protected void showAddFromSetDialog(final List<QuizletTerm> terms) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        List<String> rows = new ArrayList<>();
        final ArrayList<Course> courses = getCourseHolder().getCourses();
        for (Course course : courses) {
            rows.add(course.getTitle());
        }

        ListAdapter adapter = new ArrayAdapter<>(context, R.layout.row_text_view, rows);
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Course course = courses.get(which);
                addCardsToCourse(course, terms);
            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        String title = context.getString(R.string.dialog_choose_course);
        builder.setTitle(title);
        builder.show();
    }

    protected void addCardsToCourse(Course course, List<QuizletTerm> terms) {
        List<Card> cards = new ArrayList<>();
        for (QuizletTerm term : terms) {
            Card card = createCard(term);
            cards.add(card);
        }

        Exception exception = null;
        try {
            getCourseHolder().addNewCards(course, cards);
        } catch (Exception e) {
            exception = e;
        }

        getListener().onCardsAdded(course, exception);
    }

    protected void createCourse(String title, ArrayList<Card> cards) {
        Course course = new Course();
        course.setTitle(title);
        course.addCards(cards);

        Exception exception = null;
        try {
            getCourseHolder().addCourse(course);
        } catch (Exception e) {
            exception = e;
        }

        getListener().onCourseCreated(course, exception);
    }

    @NonNull
    protected Card createCard(QuizletTerm term) {
        Card card = new Card();
        card.setTerm(term.getTerm());
        card.setDefinition(term.getDefinition());
        card.setQuizletTerm(term);
        return card;
    }

    public @NonNull Listener<T> getListener() {
        return (Listener<T>)this.listener;
    }

    public interface Listener<T> extends ListMenuListener.Listener<T> {
        void onCourseCreated(@NonNull Course course, @Nullable Exception exception);
        void onCourseChanged(@NonNull Course course);
        void onCardsAdded(@NonNull Course course, @Nullable Exception error);
        @Nullable ViewGroup getDialogContainer();
    }
}
