package quizletfragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import com.example.alexeyglushkov.quizletservice.entities.QuizletSet;
import com.example.alexeyglushkov.quizletservice.entities.QuizletTerm;
import com.example.alexeyglushkov.wordteacher.R;
import com.example.alexeyglushkov.wordteacher.RenameAlert;

import java.util.ArrayList;
import java.util.List;

import listfragment.ListMenuListener;
import model.Card;
import model.Course;
import model.CourseHolder;

/**
 * Created by alexeyglushkov on 13.06.16.
 */
public class QuizletSetFragmentMenuListener extends QuizletFragmentMenuListener<QuizletSet> {

    public QuizletSetFragmentMenuListener(Context context, CourseHolder courseHolder, Listener listener) {
        super(context, listener, courseHolder);
    }

    private void showAddFromSetDialog(final List<QuizletTerm> terms) {
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

    private void addCardsToCourse(Course course, List<QuizletTerm> terms) {
        List<Card> cards = new ArrayList<>();
        for (QuizletTerm term : terms) {
            Card card = createCard(term);
            cards.add(card);
        }

        if (getCourseHolder().addNewCards(course, cards)) {
            getListener().onCardsAdded(course);
        }
    }

    private void onCreateCourseFromSet(QuizletSet set) {
        ArrayList<Card> cards = new ArrayList<>();
        for (QuizletTerm term : set.getTerms()) {
            Card card = createCard(term);
            cards.add(card);
        }

        createCourse(set.getTitle(), cards);
    }

    // QuizletCardsFragment.Listener


    @Override
    protected void fillMenu(final QuizletSet set, PopupMenu menu) {
        menu.getMenu().add(Menu.NONE, R.id.create_set, 0, R.string.menu_create_course);
        if (getCourseHolder().getCourses().size() > 0) {
            menu.getMenu().add(Menu.NONE, R.id.add_to_course, 0, R.string.menu_add_to_course);
        }

        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.create_set) {
                    onCreateCourseFromSet(set);

                } else if (item.getItemId() == R.id.add_to_course) {
                    ArrayList<Course> courses = getCourseHolder().getCourses();
                    if (courses.size() > 1) {
                        showAddFromSetDialog(set.getTerms());
                    } else {
                        Course course = courses.get(0);
                        addCardsToCourse(course, set.getTerms());
                    }
                }

                return false;
            }
        });
    }

    @Override
    public void onRowViewDeleted(QuizletSet data) {

    }

}
