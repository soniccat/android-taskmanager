package quizletfragments;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.alexeyglushkov.quizletservice.entities.QuizletSet;
import com.example.alexeyglushkov.quizletservice.entities.QuizletTerm;
import com.example.alexeyglushkov.wordteacher.R;
import com.example.alexeyglushkov.wordteacher.RenameAlert;

import java.util.ArrayList;

import listfragment.ListMenuListener;
import model.Card;
import model.Course;
import model.CourseHolder;

/**
 * Created by alexeyglushkov on 14.08.16.
 */
public class QuizletTermFragmentMenuListener extends QuizletFragmentMenuListener<QuizletTerm> {

    public QuizletTermFragmentMenuListener(Context context, CourseHolder courseHolder, Listener listener) {
        super(context, listener, courseHolder);
    }

    @Override
    protected void fillMenu(final QuizletTerm term, PopupMenu popupMenu) {
        popupMenu.getMenu().add(Menu.NONE, R.id.create_set, 0, R.string.menu_create_course);
        if (getCourseHolder().getCourses().size() > 0) {
            popupMenu.getMenu().add(Menu.NONE, R.id.add_to_course, 0, R.string.menu_add_to_course);
        }
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.create_set) {
                    onCreateCourseFromCard(term);

                } else if (item.getItemId() == R.id.add_to_course) {
                    ArrayList<QuizletTerm> terms = new ArrayList<>();
                    terms.add(term);
                    showAddFromSetDialog(terms);
                }

                return false;
            }
        });
    }

    @Override
    public void onRowViewDeleted(QuizletTerm data) {

    }

    private void onCreateCourseFromCard(final QuizletTerm card) {
        final RenameAlert renameAlert = new RenameAlert();
        renameAlert.setPositiveButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createCourseFromCard(card, renameAlert.getName());
            }
        });
        renameAlert.show(context, getListener().getDialogContainer());
    }

    private void createCourseFromCard(QuizletTerm quizletTerm, String name) {
        Card card = createCard(quizletTerm);
        ArrayList<Card> cards = new ArrayList<>();
        cards.add(card);

        createCourse(name, cards);
    }
}
