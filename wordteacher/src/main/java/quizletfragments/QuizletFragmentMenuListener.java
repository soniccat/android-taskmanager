package quizletfragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import com.example.alexeyglushkov.quizletservice.entities.QuizletSet;
import com.example.alexeyglushkov.quizletservice.entities.QuizletTerm;
import com.example.alexeyglushkov.wordteacher.R;

import java.util.ArrayList;
import java.util.List;

import listfragment.ListMenuListener;
import model.Card;
import model.Course;
import model.CourseHolder;

/**
 * Created by alexeyglushkov on 14.08.16.
 */
public abstract class QuizletFragmentMenuListener<T> extends ListMenuListener<T> {
    protected CourseHolder courseHolder;

    public QuizletFragmentMenuListener(Context context, Listener<T> listener, CourseHolder courseHolder) {
        super(context, listener);
        this.courseHolder = courseHolder;
    }

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

        if (getCourseHolder().addNewCards(course, cards)) {
            getListener().onCardsAdded(course);
        }
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

    public Listener<T> getListener() {
        return (Listener<T>)this.listener;
    }

    public interface Listener<T> extends ListMenuListener.Listener<T> {
        void onCourseCreated(Course course);
        //TODO: delete it
        void onCourseChanged(Course course);
        void onCardsAdded(Course course);
        ViewGroup getDialogContainer();
    }
}
