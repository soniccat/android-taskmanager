package quizletlistmodules.setlistmodule.presenter;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.view.Menu;
import android.view.MenuItem;

import com.example.alexeyglushkov.quizletservice.entities.QuizletSet;
import com.example.alexeyglushkov.quizletservice.entities.QuizletTerm;
import com.example.alexeyglushkov.wordteacher.R;

import java.util.ArrayList;

import model.Card;
import model.Course;
import model.CourseHolder;
import quizletlistmodules.QuizletPresenterMenuListener;

/**
 * Created by alexeyglushkov on 13.06.16.
 */
public class QuizletSetPresenterMenuListener extends QuizletPresenterMenuListener<QuizletSet> {

    public QuizletSetPresenterMenuListener(Context context, CourseHolder courseHolder, Listener<QuizletSet> listener) {
        super(context, listener, courseHolder);
    }

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

    private void onCreateCourseFromSet(QuizletSet set) {
        ArrayList<Card> cards = new ArrayList<>();
        for (QuizletTerm term : set.getTerms()) {
            Card card = createCard(term);
            cards.add(card);
        }

        createCourse(set.getTitle(), cards);
    }

    @Override
    public void onRowViewDeleted(QuizletSet data) {
    }
}
