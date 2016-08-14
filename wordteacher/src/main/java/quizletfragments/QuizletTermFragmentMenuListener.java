package quizletfragments;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.example.alexeyglushkov.quizletservice.entities.QuizletSet;
import com.example.alexeyglushkov.quizletservice.entities.QuizletTerm;
import com.example.alexeyglushkov.wordteacher.R;

import java.util.ArrayList;

import listfragment.ListMenuListener;
import model.Course;
import model.CourseHolder;

/**
 * Created by alexeyglushkov on 14.08.16.
 */
public class QuizletTermFragmentMenuListener extends ListMenuListener<QuizletTerm> {

    public QuizletTermFragmentMenuListener(Context context, CourseHolder courseHolder, Listener listener) {
        super(context, listener);
        this.courseHolder = courseHolder;
    }

    @Override
    protected void fillMenu(QuizletTerm data, PopupMenu popupMenu) {
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
                    ArrayList<QuizletTerm> terms = new ArrayList<QuizletTerm>();
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

    public Listener getListener() {
        return (Listener)this.listener;
    }

    public interface Listener extends ListMenuListener.Listener<QuizletSet> {
    }
}
